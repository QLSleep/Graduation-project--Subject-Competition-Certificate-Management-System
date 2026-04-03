package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.dto.CompetitionCategoryCreateDTO;
import cn.guet.competition_cert_system.service.CompetitionCategoryService;
import cn.guet.competition_cert_system.utils.Result;
import cn.guet.competition_cert_system.vo.CompetitionCategorySimpleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CompetitionCategoryController {

    private final CompetitionCategoryService competitionCategoryService;

    @GetMapping
    public Result<List<CompetitionCategorySimpleVO>> listCategories(@RequestParam(required = false) Long eventId) {
        return Result.success(competitionCategoryService.listCategories(eventId));
    }

    @PostMapping
    public Result<Long> createCategory(@RequestBody CompetitionCategoryCreateDTO dto) {
        return Result.success(competitionCategoryService.createCategory(dto));
    }
}
