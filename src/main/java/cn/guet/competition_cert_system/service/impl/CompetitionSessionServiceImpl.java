package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.mapper.CompetitionSessionMapper;
import cn.guet.competition_cert_system.service.CompetitionSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CompetitionSession 业务逻辑实现
 */
@Service
@RequiredArgsConstructor
public class CompetitionSessionServiceImpl implements CompetitionSessionService {

  private final CompetitionSessionMapper competitionSessionMapper;

}
