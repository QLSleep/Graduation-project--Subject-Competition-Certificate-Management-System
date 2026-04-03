package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.annotation.RequirePermission;
import cn.guet.competition_cert_system.common.CertAction;
import cn.guet.competition_cert_system.common.CertType;
import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.dto.CertificateExportResult;
import cn.guet.competition_cert_system.dto.CertificateReviewDTO;
import cn.guet.competition_cert_system.dto.CertificateSubmitDTO;
import cn.guet.competition_cert_system.service.CertificateService;
import cn.guet.competition_cert_system.utils.Result;
import cn.guet.competition_cert_system.vo.CertificateExportVO;
import cn.guet.competition_cert_system.vo.CertificateSimpleVO;
import cn.guet.competition_cert_system.vo.CertificateStatsVO;
import cn.guet.competition_cert_system.vo.CertificateStatusDistributionVO;
import cn.guet.competition_cert_system.vo.CertificateTrendResponseVO;
import cn.guet.competition_cert_system.vo.CollegeDistributionResponseVO;
import cn.guet.competition_cert_system.vo.UserCertificateDetailVO;
import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    /**
     * 9. 上传证书图片
     * 权限由Service层通过DataScope校验
     */
    @PostMapping("/{certId}/image")
    public Result<String> uploadCertificateImage(@PathVariable Long certId,
                                                @RequestParam("file") MultipartFile file) {
        String resourceKey = certificateService.uploadCertificateImage(certId, file);
        return Result.success(resourceKey);
    }

    /**
     * 10. 审核证书
     * 操作权限：AUDIT（SYSTEM_ADMIN / COLLEGE_ADMIN / REVIEWER）
     * 数据范围在 Service 层校验（COLLEGE_ADMIN / REVIEWER 限本学院）
     */
    @PostMapping("/{certId}/review")
    @RequirePermission(CertAction.AUDIT)
    public Result<Void> reviewCertificate(@PathVariable Long certId,
                                         @RequestBody CertificateReviewDTO dto) {
        certificateService.reviewCertificate(certId, dto.getAction(), dto.getComment());
        return Result.success(null);
    }

    /**
     * 11. 导出学院学生证书Excel
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：COLLEGE限本学院/ALL可导任意学院（Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/college/{collegeId}/export/student")
    public void exportCollegeStudentCertificates(@PathVariable Long collegeId,
                                                HttpServletResponse response) throws IOException {
        CertificateExportResult result = certificateService.exportCollegeStudentCertificates(collegeId);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(result.getFilename(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        EasyExcel.write(response.getOutputStream(), CertificateExportVO.class)
                .sheet("学生证书")
                .doWrite(result.getData());
    }

    /**
     * 12. 导出学院指导教师证书Excel
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：COLLEGE限本学院/ALL可导任意学院（Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/college/{collegeId}/export/instructor")
    public void exportCollegeInstructorCertificates(@PathVariable Long collegeId,
                                                    HttpServletResponse response) throws IOException {
        CertificateExportResult result = certificateService.exportCollegeInstructorCertificates(collegeId);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(result.getFilename(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        EasyExcel.write(response.getOutputStream(), CertificateExportVO.class)
                .sheet("指导教师证书")
                .doWrite(result.getData());
    }

    /**
     * 13. 导出个人学生证书Excel
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：SELF仅能导自己的证书（Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/my/export/student")
    public void exportMyStudentCertificates(HttpServletResponse response) throws IOException {
        CertificateExportResult result = certificateService.exportMyStudentCertificates();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(result.getFilename(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        EasyExcel.write(response.getOutputStream(), CertificateExportVO.class)
                .sheet("学生证书")
                .doWrite(result.getData());
    }

    /**
     * 14. 导出个人指导教师证书Excel
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：SELF仅能导自己的证书（Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/my/export/instructor")
    public void exportMyInstructorCertificates(HttpServletResponse response) throws IOException {
        CertificateExportResult result = certificateService.exportMyInstructorCertificates();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(result.getFilename(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        EasyExcel.write(response.getOutputStream(), CertificateExportVO.class)
                .sheet("指导教师证书")
                .doWrite(result.getData());
    }

    /**
     * 15. 导出教师指导的学生证书Excel
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：RELATED仅能导自己指导的学生证书（Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/my/students/export/student")
    public void exportTeacherGuidedStudentCertificates(HttpServletResponse response) throws IOException {
        CertificateExportResult result = certificateService.exportTeacherGuidedStudentCertificates();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(result.getFilename(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        EasyExcel.write(response.getOutputStream(), CertificateExportVO.class)
                .sheet("学生证书")
                .doWrite(result.getData());
    }

    /**
     * 16. 导出全部学生证书Excel
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：ALL仅超管可导（Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/all/export/student")
    public void exportAllStudentCertificates(HttpServletResponse response) throws IOException {
        CertificateExportResult result = certificateService.exportAllStudentCertificates();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(result.getFilename(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        EasyExcel.write(response.getOutputStream(), CertificateExportVO.class)
                .sheet("学生证书")
                .doWrite(result.getData());
    }

    /**
     * 17. 导出全部指导教师证书Excel
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：ALL仅超管可导（Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/all/export/instructor")
    public void exportAllInstructorCertificates(HttpServletResponse response) throws IOException {
        CertificateExportResult result = certificateService.exportAllInstructorCertificates();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(result.getFilename(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        EasyExcel.write(response.getOutputStream(), CertificateExportVO.class)
                .sheet("指导教师证书")
                .doWrite(result.getData());
    }

    /**
     * 18. 下载证书图片
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：SELF/RELATED/COLLEGE/ALL（Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/{certId}/image/download")
    public void downloadCertificateImage(@PathVariable Long certId, HttpServletResponse response) throws IOException {
        File file = certificateService.downloadCertificateImage(certId);

        response.setContentType("image/jpeg");
        response.setCharacterEncoding("utf-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20"));

        try (InputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 19. 导出指定学生的所有证书图片
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：SELF/RELATED/COLLEGE/ALL（Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/student/{studentId}/images/download")
    public void downloadStudentCertificateImages(@PathVariable Long studentId, HttpServletResponse response) throws IOException {
        File zipFile = certificateService.downloadStudentCertificateImages(studentId);

        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(zipFile.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        response.setContentLengthLong(zipFile.length());

        try (InputStream is = new FileInputStream(zipFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 20. 导出指定教师的所有证书图片（仅INSTRUCTOR类型）
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：SELF/COLLEGE/ALL（Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/teacher/{teacherId}/images/download")
    public void downloadInstructorCertificateImages(@PathVariable Long teacherId, HttpServletResponse response) throws IOException {
        File zipFile = certificateService.downloadInstructorCertificateImages(teacherId);

        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(zipFile.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        response.setContentLengthLong(zipFile.length());

        try (InputStream is = new FileInputStream(zipFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 21. 导出指定学院的所有证书图片
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：COLLEGE（限本学院）/ ALL（Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/college/{collegeId}/images/download")
    public void downloadCollegeCertificateImages(@PathVariable Long collegeId, HttpServletResponse response) throws IOException {
        File zipFile = certificateService.downloadCollegeCertificateImages(collegeId);

        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(zipFile.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        response.setContentLengthLong(zipFile.length());

        try (InputStream is = new FileInputStream(zipFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 22. 导出所有学生的证书图片
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：ALL（仅超管，Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/all/student/images/download")
    public void downloadAllStudentCertificateImages(HttpServletResponse response) throws IOException {
        File zipFile = certificateService.downloadAllStudentCertificateImages();

        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(zipFile.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        response.setContentLengthLong(zipFile.length());

        try (InputStream is = new FileInputStream(zipFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 23. 导出所有教师的证书图片
     * 操作权限：EXPORT（Controller层AOP校验）
     * 数据范围：ALL（仅超管，Service层校验）
     */
    @RequirePermission(CertAction.EXPORT)
    @GetMapping("/all/teacher/images/download")
    public void downloadAllTeacherCertificateImages(HttpServletResponse response) throws IOException {
        File zipFile = certificateService.downloadAllTeacherCertificateImages();

        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode(zipFile.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        response.setContentLengthLong(zipFile.length());

        try (InputStream is = new FileInputStream(zipFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    // ==================== 统计接口 ====================

    /**
     * 24. 统计当前教师的INSTRUCTOR类型证书
     * 无需特殊权限，登录即可
     */
    @GetMapping("/my/stats/instructor")
    public Result<CertificateStatsVO> getMyInstructorCertStats() {
        return Result.success(certificateService.getMyInstructorCertStats());
    }

    /**
     * 25. 统计当前教师指导过的学生数（distinct，限APPROVED/REISSUED）
     * 无需特殊权限，登录即可
     */
    @GetMapping("/my/stats/students/count")
    public Result<Long> getMyGuidedStudentCount() {
        return Result.success(certificateService.getMyGuidedStudentCount());
    }

    /**
     * 26. 统计当前学生的STUDENT类型证书
     * 无需特殊权限，登录即可
     */
    @GetMapping("/my/stats/student")
    public Result<CertificateStatsVO> getMyStudentCertStats() {
        return Result.success(certificateService.getMyStudentCertStats());
    }

    /**
     * 27. 统计学院证书（STUDENT类型）
     * 数据范围：COLLEGE限本学院，ALL可查任意（Service层校验）
     */
    @GetMapping("/college/{collegeId}/stats/student")
    public Result<CertificateStatsVO> getCollegeStudentCertStats(@PathVariable Long collegeId) {
        return Result.success(certificateService.getCollegeCertStats(collegeId, CertType.STUDENT));
    }

    /**
     * 28. 统计学院证书（INSTRUCTOR类型）
     * 数据范围：COLLEGE限本学院，ALL可查任意（Service层校验）
     */
    @GetMapping("/college/{collegeId}/stats/instructor")
    public Result<CertificateStatsVO> getCollegeInstructorCertStats(@PathVariable Long collegeId) {
        return Result.success(certificateService.getCollegeCertStats(collegeId, CertType.INSTRUCTOR));
    }

    /**
     * 29. 统计所有证书（STUDENT类型，仅超管）
     * 数据范围：ALL（Service层校验）
     */
    @GetMapping("/all/stats/student")
    public Result<CertificateStatsVO> getAllStudentCertStats() {
        return Result.success(certificateService.getAllCertStats(CertType.STUDENT));
    }

    /**
     * 30. 统计所有证书（INSTRUCTOR类型，仅超管）
     * 数据范围：ALL（Service层校验）
     */
    @GetMapping("/all/stats/instructor")
    public Result<CertificateStatsVO> getAllInstructorCertStats() {
        return Result.success(certificateService.getAllCertStats(CertType.INSTRUCTOR));
    }

    /**
     * 31. 获取30天证书趋势统计
     * 数据范围：SYSTEM_ADMIN 可查全部，COLLEGE_ADMIN 只查本学院
     * 无需特殊操作权限，登录即可访问
     * @param certType 证书类型（STUDENT/INSTRUCTOR，不传则查全部）
     * @return 趋势数据
     */
    @GetMapping("/stats/trend")
    public Result<CertificateTrendResponseVO> getCertificateTrend(
            @RequestParam(required = false) CertType certType) {
        return Result.success(certificateService.getCertificateTrend(certType));
    }

    /**
     * 32. 获取证书状态分布统计
     * 数据范围：SYSTEM_ADMIN 可查全部，COLLEGE_ADMIN 只查本学院
     * 无需特殊操作权限，登录即可访问
     * @param certType 证书类型（STUDENT/INSTRUCTOR，不传则查全部）
     * @return 状态分布（含百分比）
     */
    @GetMapping("/stats/distribution")
    public Result<CertificateStatusDistributionVO> getCertificateStatusDistribution(
            @RequestParam(required = false) CertType certType) {
        return Result.success(certificateService.getCertificateStatusDistribution(certType));
    }

    /**
     * 33. 获取证书学院分布统计（仅超管）
     * @return 各学院有效证书数量分布
     */
    @GetMapping("/stats/college/distribution")
    public Result<CollegeDistributionResponseVO> getCollegeDistribution() {
        return Result.success(certificateService.getCollegeDistribution());
    }

    /**
     * 34. 查询教师自己持有的INSTRUCTOR类型证书状态分布（个人）
     * @return 状态分布（含百分比）
     */
    @GetMapping("/my/stats/instructor/percent")
    public Result<CertificateStatusDistributionVO> getMyInstructorCertStatsWithPercent() {
        return Result.success(certificateService.getMyInstructorCertStatsWithPercent());
    }

    /**
     * 35. 查询学生自己的STUDENT类型证书状态分布（个人）
     * @return 状态分布（含百分比）
     */
    @GetMapping("/my/stats/student/percent")
    public Result<CertificateStatusDistributionVO> getMyStudentCertStatsWithPercent() {
        return Result.success(certificateService.getMyStudentCertStatsWithPercent());
    }
}
