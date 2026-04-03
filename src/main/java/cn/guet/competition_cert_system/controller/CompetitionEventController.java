package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.dto.CompetitionEventCreateDTO;
import cn.guet.competition_cert_system.dto.UpdateCompetitionEventDTO;
import cn.guet.competition_cert_system.service.CompetitionEventService;
import cn.guet.competition_cert_system.utils.Result;
import cn.guet.competition_cert_system.vo.CompetitionEventSimpleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class CompetitionEventController {

    private final CompetitionEventService competitionEventService;

    @GetMapping
    public Result<List<CompetitionEventSimpleVO>> listEvents() {
        return Result.success(competitionEventService.listEvents());
    }

    @PostMapping
    public Result<Long> createEvent(@RequestBody CompetitionEventCreateDTO dto) {
        return Result.success(competitionEventService.createEvent(dto));
    }

    /**
     * 编辑赛事
     * 权限：SYSTEM_ADMIN / COLLEGE_ADMIN
     */
    @PutMapping("/{eventId}")
    public Result<Void> updateEvent(@PathVariable Long eventId,
                                   @RequestBody UpdateCompetitionEventDTO dto) {
        competitionEventService.updateEvent(eventId, dto);
        return Result.success(null);
    }
}
