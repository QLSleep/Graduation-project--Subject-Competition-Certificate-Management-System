package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.dto.CompetitionSessionCreateDTO;
import cn.guet.competition_cert_system.service.CompetitionSessionService;
import cn.guet.competition_cert_system.utils.Result;
import cn.guet.competition_cert_system.vo.CompetitionSessionSimpleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class CompetitionSessionController {

    private final CompetitionSessionService competitionSessionService;

    @GetMapping
    public Result<List<CompetitionSessionSimpleVO>> listSessions(@RequestParam(required = false) Long eventId) {
        return Result.success(competitionSessionService.listSessions(eventId));
    }

    @PostMapping
    public Result<Long> createSession(@RequestBody CompetitionSessionCreateDTO dto) {
        return Result.success(competitionSessionService.createSession(dto));
    }
}
