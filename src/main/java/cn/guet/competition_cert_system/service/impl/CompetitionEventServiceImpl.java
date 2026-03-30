package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.dto.CompetitionEventCreateDTO;
import cn.guet.competition_cert_system.entity.CompetitionEvent;
import cn.guet.competition_cert_system.mapper.CompetitionEventMapper;
import cn.guet.competition_cert_system.service.CompetitionEventService;
import cn.guet.competition_cert_system.vo.CompetitionEventSimpleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionEventServiceImpl implements CompetitionEventService {

    private final CompetitionEventMapper competitionEventMapper;

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
    public Long createEvent(CompetitionEventCreateDTO dto) {
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
        return event.getId();
    }
}
