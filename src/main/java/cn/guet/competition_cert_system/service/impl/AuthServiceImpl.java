package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.common.DataScope;
import cn.guet.competition_cert_system.config.PermissionConfig;
import cn.guet.competition_cert_system.entity.User;
import cn.guet.competition_cert_system.mapper.UserMapper;
import cn.guet.competition_cert_system.mapper.UserRoleMapper;
import cn.guet.competition_cert_system.service.AuthService;
import cn.guet.competition_cert_system.service.RedisService;
import cn.guet.competition_cert_system.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RedisService redisService;
    private final JWTUtil jwtUtil;
    private final PermissionConfig permissionConfig;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Override
    public LoginResult login(String username, String password) {
        // 1. 查询用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 校验密码（这里简单处理，生产环境用 BCrypt）
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 查询用户角色
        List<String> roles = userRoleMapper.selectRoleNamesByUserId(user.getId());
        if (roles == null || roles.isEmpty()) {
            throw new RuntimeException("该用户未分配角色");
        }

        // 4. 根据角色获取权限（取第一个角色的权限）
        String primaryRole = roles.get(0);
        PermissionConfig.UserPermission permission = permissionConfig.getPermissionByRole(primaryRole);
        if (permission == null) {
            throw new RuntimeException("该角色未配置权限");
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
            throw new RuntimeException("refreshToken无效或已过期");
        }

        // 2. 确认是 refreshToken 类型
        String tokenType = jwtUtil.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new RuntimeException("token类型错误");
        }

        // 3. 取 userId
        Long userId = jwtUtil.extractUserId(refreshToken);

        // 4. Redis 校验
        if (!redisService.validateRefreshToken(userId, refreshToken)) {
            throw new RuntimeException("refreshToken已被撤销或失效");
        }

        // 5. 查用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 6. 查角色
        List<String> roles = userRoleMapper.selectRoleNamesByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            throw new RuntimeException("该用户未分配角色");
        }

        // 7. 根据角色获取权限
        String primaryRole = roles.get(0);
        PermissionConfig.UserPermission permission = permissionConfig.getPermissionByRole(primaryRole);
        if (permission == null) {
            throw new RuntimeException("该角色未配置权限");
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
}
