package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.common.DataScope;
import cn.guet.competition_cert_system.common.ResultCode;
import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.config.PermissionConfig;
import cn.guet.competition_cert_system.config.PermissionConfig.UserPermission;
import cn.guet.competition_cert_system.entity.User;
import cn.guet.competition_cert_system.exception.BusinessException;
import cn.guet.competition_cert_system.mapper.UserMapper;
import cn.guet.competition_cert_system.mapper.UserRoleMapper;
import cn.guet.competition_cert_system.service.AuthService;
import cn.guet.competition_cert_system.service.MailService;
import cn.guet.competition_cert_system.service.RedisService;
import cn.guet.competition_cert_system.utils.JWTUtil;
import cn.guet.competition_cert_system.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RedisService redisService;
    private final JWTUtil jwtUtil;
    private final PermissionConfig permissionConfig;
    private final MailService mailService;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Override
    public LoginResult login(String username, String password) {
        // 1. 查询用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 校验密码
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "密码错误");
        }

        // 3. 校验账户状态
        if (user.getEnabled() == null || !user.getEnabled()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED_ERROR, "账户已被禁用");
        }

        // 4. 查询用户角色
        List<String> roles = userRoleMapper.selectRoleNamesByUserId(user.getId());
        if (roles == null || roles.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NO_ROLE);
        }

        // 4. 根据角色获取权限（取第一个角色的权限）
        String primaryRole = roles.get(0);
        UserPermission permission = permissionConfig.getPermissionByRole(primaryRole);
        if (permission == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_CONFIGURED);
        }

        DataScope dataScope = permission.getDataScope();
        List<String> permissions = permission.getPermissions();

        // 5. 生成 tokens
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                roles,
                user.getCollegeId(),
                dataScope.name(),
                permissions
        );

        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 6. 存 Redis
        redisService.setRefreshToken(user.getId(), refreshToken, refreshExpiration / 1000);

        return new LoginResult(accessToken, refreshToken);
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        // 1. 验证 token 格式
        if (!Boolean.TRUE.equals(jwtUtil.validateToken(refreshToken))) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_EXPIRED);
        }

        // 2. 确认是 refreshToken 类型
        String tokenType = jwtUtil.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED_ERROR, "token类型错误");
        }

        // 3. 取 userId
        Long userId = jwtUtil.extractUserId(refreshToken);

        // 4. Redis 校验
        if (!redisService.validateRefreshToken(userId, refreshToken)) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_EXPIRED, "refreshToken已被撤销");
        }

        // 5. 查用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 6. 查角色
        List<String> roles = userRoleMapper.selectRoleNamesByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NO_ROLE);
        }

        // 7. 根据角色获取权限
        String primaryRole = roles.get(0);
        PermissionConfig.UserPermission permission = permissionConfig.getPermissionByRole(primaryRole);
        if (permission == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_CONFIGURED);
        }

        // 8. 生成新 accessToken
        return jwtUtil.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                roles,
                user.getCollegeId(),
                permission.getDataScope().name(),
                permission.getPermissions()
        );
    }

    @Override
    public void logout(Long userId) {
        redisService.deleteRefreshToken(userId);
    }

    @Override
    public void sendRegisterCode(String email) {
        // 1. 生成6位字母数字验证码（大写字母）
        String code = generateCode(6);
        // 2. 存Redis，5分钟有效期
        redisService.setEmailCode(email, code, 300);
        // 3. 发送邮件
        mailService.sendRegisterCode(email, code);
    }

    @Override
    public void sendResetCode(String username, String email) {
        // 1. 校验用户存在且邮箱匹配
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!email.equalsIgnoreCase(user.getEmail())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "邮箱不匹配");
        }

        // 2. 生成验证码
        String code = generateCode(6);
        // 3. 存Redis，5分钟有效期，key用 reset:{email}
        redisService.setEmailCode("reset:" + email, code, 300);
        // 4. 发送邮件
        mailService.sendResetPasswordCode(email, code);
        System.out.println("重置密码的验证码: "+username+"  "+code);//调试用
    }

    @Override
    public void resetPassword(String username, String email, String code, String newPassword) {
        // 1. 校验用户存在且邮箱匹配
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!email.equalsIgnoreCase(user.getEmail())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "邮箱不匹配");
        }

        // 2. 校验验证码
        String storedCode = redisService.getEmailCode("reset:" + email);
        if (storedCode == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "验证码已过期");
        }
        if (!storedCode.equalsIgnoreCase(code)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "验证码错误");
        }

        // 3. 更新密码
        user.setPassword(PasswordUtil.encrypt(newPassword));
        userMapper.updateById(user);

        // 4. 删除验证码
        redisService.deleteEmailCode("reset:" + email);
    }

    @Override
    public void sendUpdateEmailCode() {
        // 1. 获取当前用户
        Long userId = AuthContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 生成验证码
        String code = generateCode(6);
        // 3. 存Redis，5分钟有效期，key用 update_email:{userId}
        redisService.setEmailCode("update_email:" + userId, code, 300);
        // 4. 发送邮件到当前邮箱
        mailService.sendResetPasswordCode(user.getEmail(), code);
        System.out.println("重置邮箱的验证码: "+user.getEmail()+"  "+code);//调试用
    }

    @Override
    public void updateEmail(String code, String newEmail) {
        // 1. 获取当前用户
        Long userId = AuthContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 校验验证码
        String storedCode = redisService.getEmailCode("update_email:" + userId);
        if (storedCode == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "验证码已过期");
        }
        if (!storedCode.equalsIgnoreCase(code)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "验证码错误");
        }

        // 3. 校验新邮箱是否已被使用
        User existingUser = userMapper.selectByEmail(newEmail);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }

        // 4. 更新邮箱
        user.setEmail(newEmail);
        userMapper.updateById(user);

        // 5. 删除验证码
        redisService.deleteEmailCode("update_email:" + userId);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String confirmPassword) {
        // 1. 获取当前用户
        Long userId = AuthContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 校验旧密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "旧密码错误");
        }

        // 3. 校验两次密码一致
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException(ResultCode.PASSWORD_MISMATCH);
        }

        // 4. 更新密码
        user.setPassword(PasswordUtil.encrypt(newPassword));
        userMapper.updateById(user);
    }

    private String generateCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
