package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.dto.SubjectGroupCreateDTO;
import cn.guet.competition_cert_system.service.SubjectGroupService;
import cn.guet.competition_cert_system.utils.Result;
import cn.guet.competition_cert_system.vo.SubjectGroupSimpleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class SubjectGroupController {

    private final SubjectGroupService subjectGroupService;

    @GetMapping
    public Result<List<SubjectGroupSimpleVO>> listGroups(@RequestParam(required = false) Long eventId) {
        return Result.success(subjectGroupService.listGroups(eventId));
    }

    @PostMapping
    public Result<Long> createGroup(@RequestBody SubjectGroupCreateDTO dto) {
        return Result.success(subjectGroupService.createGroup(dto));
    }
}
