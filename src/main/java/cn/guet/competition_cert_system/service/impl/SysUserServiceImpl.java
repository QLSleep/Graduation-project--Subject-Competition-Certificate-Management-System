package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.mapper.UserMapper;
import cn.guet.competition_cert_system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * SysUser 业务逻辑实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

  private final UserMapper userMapper;

}
