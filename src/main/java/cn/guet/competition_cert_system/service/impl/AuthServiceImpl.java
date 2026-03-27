package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.mapper.UserMapper;
import cn.guet.competition_cert_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Auth 业务逻辑实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserMapper userMapper;

}
