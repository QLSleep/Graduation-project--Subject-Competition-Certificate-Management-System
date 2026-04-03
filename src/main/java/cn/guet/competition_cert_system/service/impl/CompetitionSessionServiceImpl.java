package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.dto.CompetitionSessionCreateDTO;
import cn.guet.competition_cert_system.entity.CompetitionSession;
import cn.guet.competition_cert_system.mapper.CompetitionSessionMapper;
import cn.guet.competition_cert_system.service.CompetitionSessionService;
import cn.guet.competition_cert_system.vo.CompetitionSessionSimpleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionSessionServiceImpl implements CompetitionSessionService {

    private final CompetitionSessionMapper competitionSessionMapper;

    @Override
    public List<CompetitionSessionSimpleVO> listSessions(Long eventId) {
        return competitionSessionMapper.selectSessionList(eventId);
    }

    @Override
    public Long createSession(CompetitionSessionCreateDTO dto) {
        CompetitionSession session = new CompetitionSession();
        session.setSessionName(dto.getSessionName());
        session.setYear(dto.getYear());
        session.setStartDate(dto.getStartDate());
        session.setEndDate(dto.getEndDate());
        session.setEventId(dto.getEventId());
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        competitionSessionMapper.insert(session);
        return session.getId();
    }
}
