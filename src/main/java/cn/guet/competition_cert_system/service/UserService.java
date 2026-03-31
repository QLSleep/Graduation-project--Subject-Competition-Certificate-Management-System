package cn.guet.competition_cert_system.service;

import cn.guet.competition_cert_system.dto.UserCreateDTO;
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
}
