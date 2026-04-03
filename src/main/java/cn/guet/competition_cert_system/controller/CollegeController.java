package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.service.CollegeService;
import cn.guet.competition_cert_system.utils.Result;
import cn.guet.competition_cert_system.vo.CollegeSimpleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/college")
@RequiredArgsConstructor
public class CollegeController {

    private final CollegeService collegeService;

    @GetMapping
    public Result<List<CollegeSimpleVO>> listAllColleges() {
        return Result.success(collegeService.listAllColleges());
    }
}
