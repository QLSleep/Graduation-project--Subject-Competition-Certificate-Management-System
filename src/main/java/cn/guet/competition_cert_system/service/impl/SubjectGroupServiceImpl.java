package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.mapper.SubjectGroupMapper;
import cn.guet.competition_cert_system.service.SubjectGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * SubjectGroup 业务逻辑实现
 */
@Service
@RequiredArgsConstructor
public class SubjectGroupServiceImpl implements SubjectGroupService {

  private final SubjectGroupMapper subjectGroupMapper;

}
