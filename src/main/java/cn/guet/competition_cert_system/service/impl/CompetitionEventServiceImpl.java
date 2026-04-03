package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.common.ResultCode;
import cn.guet.competition_cert_system.dto.CompetitionEventCreateDTO;
import cn.guet.competition_cert_system.dto.UpdateCompetitionEventDTO;
import cn.guet.competition_cert_system.entity.CompetitionCategory;
import cn.guet.competition_cert_system.entity.CompetitionEvent;
import cn.guet.competition_cert_system.entity.CompetitionSession;
import cn.guet.competition_cert_system.entity.SubjectGroup;
import cn.guet.competition_cert_system.exception.BusinessException;
import cn.guet.competition_cert_system.exception.NotFoundException;
import cn.guet.competition_cert_system.mapper.CompetitionCategoryMapper;
import cn.guet.competition_cert_system.mapper.CompetitionEventMapper;
import cn.guet.competition_cert_system.mapper.CompetitionSessionMapper;
import cn.guet.competition_cert_system.mapper.SubjectGroupMapper;
import cn.guet.competition_cert_system.service.CompetitionEventService;
import cn.guet.competition_cert_system.vo.CompetitionEventSimpleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionEventServiceImpl implements CompetitionEventService {

    private final CompetitionEventMapper competitionEventMapper;
    private final CompetitionSessionMapper competitionSessionMapper;
    private final SubjectGroupMapper subjectGroupMapper;
    private final CompetitionCategoryMapper competitionCategoryMapper;

    @Override
    public List<CompetitionEventSimpleVO> listEvents() {
        return competitionEventMapper.selectList(new LambdaQueryWrapper<>())
                .stream()
                .map(e -> {
                    CompetitionEventSimpleVO vo = new CompetitionEventSimpleVO();
                    vo.setId(e.getId());
                    vo.setEventName(e.getEventName());
                    vo.setAbbreviation(e.getAbbreviation());
                    vo.setLevel(e.getLevel());
                    return vo;
                })
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEvent(CompetitionEventCreateDTO dto) {
        // 1. 创建赛事
        CompetitionEvent event = new CompetitionEvent();
        event.setEventName(dto.getEventName());
        event.setAbbreviation(dto.getAbbreviation());
        event.setLevel(dto.getLevel());
        event.setSponsor(dto.getSponsor());
        event.setOrganizer(dto.getOrganizer());
        event.setDescription(dto.getDescription());
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        competitionEventMapper.insert(event);

        // 2. 创建赛届
        if (dto.getSessionName() != null && !dto.getSessionName().isBlank()) {
            CompetitionSession session = new CompetitionSession();
            session.setSessionName(dto.getSessionName());
            session.setYear(dto.getSessionYear());
            session.setStartDate(dto.getStartDate());
            session.setEndDate(dto.getEndDate());
            session.setEventId(event.getId());
            session.setCreatedAt(LocalDateTime.now());
            session.setUpdatedAt(LocalDateTime.now());
            competitionSessionMapper.insert(session);
        }

        // 3. 创建科目组别
        if (dto.getGroupName() != null && !dto.getGroupName().isBlank()) {
            SubjectGroup group = new SubjectGroup();
            group.setGroupName(dto.getGroupName());
            group.setDescription(dto.getGroupDescription());
            group.setEventId(event.getId());
            group.setCreatedAt(LocalDateTime.now());
            group.setUpdatedAt(LocalDateTime.now());
            subjectGroupMapper.insert(group);
        }

        // 4. 创建赛类
        if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {
            CompetitionCategory category = new CompetitionCategory();
            category.setCategoryName(dto.getCategoryName());
            category.setDescription(dto.getCategoryDescription());
            category.setEventId(event.getId());
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            competitionCategoryMapper.insert(category);
        }

        return event.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEvent(Long eventId, UpdateCompetitionEventDTO dto) {
        // 1. 权限校验：SYSTEM_ADMIN 或 COLLEGE_ADMIN
        String primaryRole = AuthContext.getRoles().get(0);
        if (!"SYSTEM_ADMIN".equals(primaryRole) && !"COLLEGE_ADMIN".equals(primaryRole)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 2. 查询赛事是否存在
        CompetitionEvent event = competitionEventMapper.selectById(eventId);
        if (event == null) {
            throw new NotFoundException("赛事不存在");
        }

        // 3. abbreviation 唯一性校验（排除自身）
        if (dto.getAbbreviation() != null
                && !dto.getAbbreviation().equals(event.getAbbreviation())) {
            LambdaQueryWrapper<CompetitionEvent> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CompetitionEvent::getAbbreviation, dto.getAbbreviation());
            if (competitionEventMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "赛事缩写已存在");
            }
        }

        // 4. 非空字段更新
        if (dto.getEventName() != null) {
            event.setEventName(dto.getEventName());
        }
        if (dto.getAbbreviation() != null) {
            event.setAbbreviation(dto.getAbbreviation());
        }
        if (dto.getLevel() != null) {
            event.setLevel(dto.getLevel());
        }
        if (dto.getSponsor() != null) {
            event.setSponsor(dto.getSponsor());
        }
        if (dto.getOrganizer() != null) {
            event.setOrganizer(dto.getOrganizer());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        event.setUpdatedAt(LocalDateTime.now());
        competitionEventMapper.updateById(event);
    }
}
