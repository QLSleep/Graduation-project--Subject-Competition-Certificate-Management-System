package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.mapper.CollegeMapper;
import cn.guet.competition_cert_system.service.CollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * College 业务逻辑实现
 */
@Service
@RequiredArgsConstructor
public class CollegeServiceImpl implements CollegeService {

  private final CollegeMapper collegeMapper;

}
