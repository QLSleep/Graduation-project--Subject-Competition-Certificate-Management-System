package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.mapper.CompetitionCategoryMapper;
import cn.guet.competition_cert_system.service.CompetitionCategoryService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * CompetitionCategory 服务实现类
 */
@Service
@RequiredArgsConstructor
public class CompetitionCategoryServiceImpl implements CompetitionCategoryService {

    private final CompetitionCategoryMapper competitionCategoryMapper;
}
