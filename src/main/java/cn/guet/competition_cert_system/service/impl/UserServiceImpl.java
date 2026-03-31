package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.common.DataScope;
import cn.guet.competition_cert_system.common.ResultCode;
import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.dto.UserCreateDTO;
import cn.guet.competition_cert_system.dto.UserRegisterDTO;
import cn.guet.competition_cert_system.entity.Role;
import cn.guet.competition_cert_system.entity.User;
import cn.guet.competition_cert_system.exception.BusinessException;
import cn.guet.competition_cert_system.mapper.CollegeMapper;
import cn.guet.competition_cert_system.mapper.RoleMapper;
import cn.guet.competition_cert_system.mapper.UserMapper;
import cn.guet.competition_cert_system.mapper.UserRoleMapper;
import cn.guet.competition_cert_system.service.RedisService;
import cn.guet.competition_cert_system.service.UserService;
import cn.guet.competition_cert_system.utils.PasswordUtil;
import cn.guet.competition_cert_system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final CollegeMapper collegeMapper;
    private final RedisService redisService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO dto) {
        // 1. 校验验证码
        String storedCode = redisService.getEmailCode(dto.getEmail());
        if (storedCode == null) {
            throw new BusinessException(ResultCode.VERIFY_CODE_ERROR, "验证码已过期");
        }
        if (!storedCode.equalsIgnoreCase(dto.getVerifyCode())) {
            throw new BusinessException(ResultCode.VERIFY_CODE_ERROR, "验证码错误");
        }

        // 2. 校验两次密码一致
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_MISMATCH);
        }

        // 3. 校验 type
        if (!"STUDENT".equals(dto.getType()) && !"TEACHER".equals(dto.getType())) {
            throw new BusinessException(ResultCode.INVALID_USER_TYPE);
        }

        // 4. 校验 username 唯一
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 5. 校验 studentId/teacherId 唯一
        if ("STUDENT".equals(dto.getType())) {
            if (dto.getStudentId() == null || dto.getStudentId().isBlank()) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "学号不能为空");
            }
            if (userMapper.findByStudentId(dto.getStudentId()) != null) {
                throw new BusinessException(ResultCode.STUDENT_ID_EXISTS);
            }
        } else {
            if (dto.getTeacherId() == null || dto.getTeacherId().isBlank()) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "工号不能为空");
            }
            if (userMapper.findByTeacherId(dto.getTeacherId()) != null) {
                throw new BusinessException(ResultCode.TEACHER_ID_EXISTS);
            }
        }

        // 6. 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encrypt(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setCollegeId(dto.getCollegeId());
        user.setStudentId("STUDENT".equals(dto.getType()) ? dto.getStudentId() : null);
        user.setTeacherId("TEACHER".equals(dto.getType()) ? dto.getTeacherId() : null);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);

        // 7. 分配角色
        Role role = roleMapper.selectByRoleName(dto.getType());
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_CONFIGURED);
        }
        userRoleMapper.insert(user.getId(), role.getId());

        // 8. 删除验证码
        redisService.deleteEmailCode(dto.getEmail());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserCreateDTO dto) {
        // 1. 获取当前用户信息
        DataScope dataScope = AuthContext.getDataScope();
        String primaryRole = AuthContext.getRoles().get(0);
        Long userCollegeId = AuthContext.getCollegeId();

        // 2. 权限校验：只有超管和院管可以创建用户
        if (!"SYSTEM_ADMIN".equals(primaryRole) && !"COLLEGE_ADMIN".equals(primaryRole)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 3. 院管不能创建管理员
        if ("COLLEGE_ADMIN".equals(primaryRole) && "COLLEGE_ADMIN".equals(dto.getType())) {
            throw new BusinessException(ResultCode.COLLEGE_ADMIN_CANNOT_CREATE_ADMIN);
        }

        // 4. 院管创建时自动绑定学院
        Long collegeId = dto.getCollegeId();
        if ("COLLEGE_ADMIN".equals(primaryRole)) {
            collegeId = userCollegeId;
        }

        // 5. 校验 type
        if (!"STUDENT".equals(dto.getType()) && !"TEACHER".equals(dto.getType()) && !"COLLEGE_ADMIN".equals(dto.getType())) {
            throw new BusinessException(ResultCode.INVALID_USER_TYPE);
        }

        // 6. 校验 username 唯一
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 7. 校验 studentId/teacherId 唯一
        if ("STUDENT".equals(dto.getType())) {
            if (dto.getStudentId() == null || dto.getStudentId().isBlank()) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "学号不能为空");
            }
            if (userMapper.findByStudentId(dto.getStudentId()) != null) {
                throw new BusinessException(ResultCode.STUDENT_ID_EXISTS);
            }
        } else if ("TEACHER".equals(dto.getType())) {
            if (dto.getTeacherId() == null || dto.getTeacherId().isBlank()) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "工号不能为空");
            }
            if (userMapper.findByTeacherId(dto.getTeacherId()) != null) {
                throw new BusinessException(ResultCode.TEACHER_ID_EXISTS);
            }
        }

        // 8. 自动生成密码 (Username + "123456")
        String autoPassword = dto.getUsername() + "123456";

        // 9. 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encrypt(autoPassword));
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setCollegeId(collegeId);
        user.setStudentId("STUDENT".equals(dto.getType()) ? dto.getStudentId() : null);
        user.setTeacherId("TEACHER".equals(dto.getType()) ? dto.getTeacherId() : null);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);

        // 10. 分配角色
        Role role = roleMapper.selectByRoleName(dto.getType());
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_CONFIGURED);
        }
        userRoleMapper.insert(user.getId(), role.getId());
    }

    @Override
    public UserVO getCurrentUser() {
        return getUserById(AuthContext.getUserId());
    }

    @Override
    public UserVO getUserById(Long userId) {
        // 1. 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 2. 权限校验
        Long currentUserId = AuthContext.getUserId();
        if (!userId.equals(currentUserId)) {
            String primaryRole = AuthContext.getRoles().get(0);
            if (!"SYSTEM_ADMIN".equals(primaryRole) && !"COLLEGE_ADMIN".equals(primaryRole)) {
                throw new BusinessException(ResultCode.FORBIDDEN);
            }
            if ("COLLEGE_ADMIN".equals(primaryRole) &&
                !user.getCollegeId().equals(AuthContext.getCollegeId())) {
                throw new BusinessException(ResultCode.FORBIDDEN);
            }
        }

        // 3. 组装 VO
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setStudentId(user.getStudentId());
        vo.setTeacherId(user.getTeacherId());
        vo.setCollegeId(user.getCollegeId());
        vo.setCollegeName(collegeMapper.selectNameById(user.getCollegeId()));
        vo.setEnabled(user.getEnabled());
        vo.setEnabledText(Boolean.TRUE.equals(user.getEnabled()) ? "激活" : "禁用");
        vo.setRoles(userRoleMapper.selectRoleNamesByUserId(userId));
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }
}
