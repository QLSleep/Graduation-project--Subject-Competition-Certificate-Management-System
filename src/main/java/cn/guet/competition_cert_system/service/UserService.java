package cn.guet.competition_cert_system.service;

import java.util.List;

import cn.guet.competition_cert_system.dto.UserCreateDTO;
import cn.guet.competition_cert_system.dto.UpdateUserDTO;
import cn.guet.competition_cert_system.dto.UserRegisterDTO;
import cn.guet.competition_cert_system.vo.UserVO;

public interface UserService {

    /**
     * 自助注册（学生/教师）
     * @param dto 注册信息
     */
    void register(UserRegisterDTO dto);

    /**
     * 管理员创建用户
     * @param dto 用户信息
     */
    void createUser(UserCreateDTO dto);

    /**
     * 获取当前用户信息
     * @return 用户VO
     */
    UserVO getCurrentUser();

    /**
     * 获取指定用户信息
     * @param userId 用户ID
     * @return 用户VO
     */
    UserVO getUserById(Long userId);

    /**
     * 列表查看用户
     * 超管：查询所有非超管用户
     * 院管：查询本院师生
     * @return 用户列表
     */
    List<UserVO> listUsers();

    /**
     * 修改用户信息
     * @param userId 用户ID
     * @param dto 更新信息
     */
    void updateUser(Long userId, UpdateUserDTO dto);

    /**
     * 启用/禁用用户
     * @param userId 用户ID
     * @param enabled 是否启用
     */
    void setUserEnabled(Long userId, Boolean enabled);
}
