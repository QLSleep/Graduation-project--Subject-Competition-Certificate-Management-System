package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.mapper.RoleMapper;
import cn.guet.competition_cert_system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * SysRole 业务逻辑实现
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

  private final RoleMapper roleMapper;

}
