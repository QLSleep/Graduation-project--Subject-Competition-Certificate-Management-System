package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.annotation.RequirePermission;
import cn.guet.competition_cert_system.common.CertAction;
import cn.guet.competition_cert_system.dto.CollegeCreateDTO;
import cn.guet.competition_cert_system.dto.UpdateCollegeDTO;
import cn.guet.competition_cert_system.service.CollegeService;
import cn.guet.competition_cert_system.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/college")
@RequiredArgsConstructor
public class AdminCollegeController {

    private final CollegeService collegeService;

    /**
     * 新增学院
     * 只有超管可以使用
     */
    @PostMapping
    @RequirePermission(CertAction.CREATE)
    public Result<Void> createCollege(@RequestBody CollegeCreateDTO dto) {
        try {
            collegeService.createCollege(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改学院信息
     * 只有超管可以使用
     */
    @PutMapping("/{collegeId}")
    @RequirePermission(CertAction.EDIT)
    public Result<Void> updateCollege(@PathVariable Long collegeId, @RequestBody UpdateCollegeDTO dto) {
        try {
            collegeService.updateCollege(collegeId, dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
