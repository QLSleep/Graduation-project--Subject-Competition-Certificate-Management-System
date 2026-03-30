package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.annotation.RequirePermission;
import cn.guet.competition_cert_system.common.CertAction;
import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.dto.CertificateSubmitDTO;
import cn.guet.competition_cert_system.service.CertificateService;
import cn.guet.competition_cert_system.utils.Result;
import cn.guet.competition_cert_system.vo.CertificateSimpleVO;
import cn.guet.competition_cert_system.vo.UserCertificateDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 证书接口
 * 操作权限（@RequirePermission）在 Controller 层通过 AOP 校验
 * 数据范围在 Service 层通过 AuthContext.getDataScope() 校验
 */
@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    /**
     * 提交证书申请
     * 操作权限：CREATE（由 AOP 校验）
     * 数据范围：Service 层自动使用当前用户的 collegeId
     */
    @PostMapping
    @RequirePermission(CertAction.CREATE)
    public Result<Void> submitCertificate(@RequestBody CertificateSubmitDTO dto) {
        certificateService.submitCertificateApplication(dto);
        return Result.success(null);
    }

    /**
     * 1. 查询当前用户自己的证书列表
     */
    @GetMapping("/my")
    public Result<List<CertificateSimpleVO>> getMyCertificates() {
        Long userId = AuthContext.getUserId();
        return Result.success(certificateService.getUserCertificates(userId));
    }

    /**
     * 2. 查询证书详情
     */
    @GetMapping("/{certId}")
    public Result<UserCertificateDetailVO> getCertificateDetail(@PathVariable Long certId) {
        return Result.success(certificateService.getCertificateDetail(certId));
    }

    /**
     * 3. 查询当前用户指导的学生证书列表（仅STUDENT类型）
     */
    @GetMapping("/my/students")
    public Result<List<CertificateSimpleVO>> getMyStudentCertificates() {
        Long teacherUserId = AuthContext.getUserId();
        return Result.success(certificateService.getStudentCertificatesByTeacher(teacherUserId));
    }

    /**
     * 4. 查询指定学院的所有证书（管理员用）
     * 数据范围在 Service 层校验（COLLEGE/ALL）
     */
    @GetMapping("/college/{collegeId}")
    public Result<List<CertificateSimpleVO>> getCollegeCertificates(@PathVariable Long collegeId) {
        return Result.success(certificateService.getCollegeCertificates(collegeId));
    }

    /**
     * 5. 查询所有证书（仅超管）
     * 数据范围在 Service 层校验（ALL）
     */
    @GetMapping("/all")
    public Result<List<CertificateSimpleVO>> getAllCertificates() {
        return Result.success(certificateService.getAllCertificates());
    }

    /**
     * 6. 查询待审核证书列表（审核员/管理员）
     * 数据范围在 Service 层校验（COLLEGE/ALL）
     */
    @GetMapping("/pending")
    @RequirePermission(CertAction.AUDIT)
    public Result<List<CertificateSimpleVO>> getPendingReviewCertificates(
            @RequestParam(required = false) Long collegeId) {
        return Result.success(certificateService.getPendingReviewCertificates(collegeId));
    }

    /**
     * 7. 更新证书（管理员直接更新）
     * 操作权限：EDIT（SYSTEM_ADMIN / COLLEGE_ADMIN）
     * 数据范围：COLLEGE_ADMIN 只能改自己学院的
     */
    @PutMapping("/{certId}")
    @RequirePermission(CertAction.EDIT)
    public Result<Void> updateCertificate(@PathVariable Long certId,
                                          @RequestBody CertificateSubmitDTO dto) {
        certificateService.updateCertificate(certId, dto);
        return Result.success(null);
    }

    /**
     * 8. 删除证书（仅超管）
     * 操作权限：DELETE（SYSTEM_ADMIN）
     */
    @DeleteMapping("/{certId}")
    @RequirePermission(CertAction.DELETE)
    public Result<Void> deleteCertificate(@PathVariable Long certId) {
        certificateService.deleteCertificate(certId);
        return Result.success(null);
    }
}
