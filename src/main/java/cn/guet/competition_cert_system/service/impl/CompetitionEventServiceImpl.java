package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.mapper.CompetitionEventMapper;
import cn.guet.competition_cert_system.service.CompetitionEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CompetitionEvent 业务逻辑实现
 */
@Service
@RequiredArgsConstructor
public class CompetitionEventServiceImpl implements CompetitionEventService {

  private final CompetitionEventMapper competitionEventMapper;

}
