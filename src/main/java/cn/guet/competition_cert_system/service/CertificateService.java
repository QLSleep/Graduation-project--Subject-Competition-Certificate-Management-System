package cn.guet.competition_cert_system.service;

import cn.guet.competition_cert_system.common.CertType;
import cn.guet.competition_cert_system.dto.CertificateExportResult;
import cn.guet.competition_cert_system.dto.CertificateSubmitDTO;
import cn.guet.competition_cert_system.vo.CertificateExportVO;
import cn.guet.competition_cert_system.vo.CertificateSimpleVO;
import cn.guet.competition_cert_system.vo.CertificateStatsVO;
import cn.guet.competition_cert_system.vo.CertificateStatusDistributionVO;
import cn.guet.competition_cert_system.vo.CertificateTrendResponseVO;
import cn.guet.competition_cert_system.vo.CollegeDistributionResponseVO;
import cn.guet.competition_cert_system.vo.UserCertificateDetailVO;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface CertificateService {

    // ==================== 查询操作 ====================

    /**
     * 查看指定单个用户证书列表（精简字段，列表用）
     * @param userId 用户ID
     * @return 精简 CertificateSimpleVO 列表
     */
    List<CertificateSimpleVO> getUserCertificates(Long userId);

    /**
     * 查看指定个人单张证书详情（完整字段，详情用）
     * @param certId 证书ID
     * @return 完整 UserCertificateDetailVO
     */
    UserCertificateDetailVO getCertificateDetail(Long certId);

    /**
     * 查看指定教师所指导的学生的证书列表（精简字段，列表用）
     * @param teacherUserId 教师用户ID
     * @return 精简 CertificateSimpleVO 列表
     */
    List<CertificateSimpleVO> getStudentCertificatesByTeacher(Long teacherUserId);

    /**
     * 查看指定学院所有证书（精简字段，列表用）
     * - STUDENT 证书：按证书聚合（多个学生）
     * - INSTRUCTOR 证书：不聚合，每个教师单独一条记录
     * 注意：权限校验在拦截器/Controller 层完成，Service 只做业务逻辑
     * @param collegeId 学院ID
     * @return 精简 CertificateSimpleVO 列表
     */
    List<CertificateSimpleVO> getCollegeCertificates(Long collegeId);

    /**
     * 查看所有证书（仅超管可访问 ALL 权限，精简字段，列表用）
     * - STUDENT 类型：按证书聚合（多个学生）
     * - INSTRUCTOR 类型：不聚合，每个教师单独一条记录
     * @return 精简 CertificateSimpleVO 列表
     */
    List<CertificateSimpleVO> getAllCertificates();

    // ==================== 待审核查询 ====================

    /**
     * 查看待审核证书列表（审核员/管理员用）
     * @param collegeId 学院ID（超管传null查全部）
     * @return 待审核证书列表
     */
    List<CertificateSimpleVO> getPendingReviewCertificates(Long collegeId);

    // ==================== 创建操作 ====================

    /**
     * 提交新证书申请
     * @param dto 证书提交信息
     */
    void submitCertificateApplication(CertificateSubmitDTO dto);


    // ==================== 编辑操作 ====================

    /**
     * 更新证书（ADMIN直接更新，含关联数据重建）
     * @param certId 证书ID
     * @param dto 更新内容
     */
    void updateCertificate(Long certId, CertificateSubmitDTO dto);


    // ==================== 删除操作 ====================

    /**
     * 删除证书（SYSTEM_ADMIN权限，物理删除）
     * @param certId 证书ID
     */
    void deleteCertificate(Long certId);


    // ==================== 上传操作 ====================

    /**
     * 上传证书图片
     * @param certId 证书ID
     * @param file 图片文件
     * @return 生成的resourceKey
     */
    String uploadCertificateImage(Long certId, MultipartFile file);


    // ==================== 审核操作 ====================

    /**
     * 审核证书
     * @param certId 证书ID
     * @param action 审核动作（APPROVE/REJECT/REJECT_NO_RESUBMIT/REISSUE/INVALID）
     * @param comment 备注/驳回原因/作废原因
     */
    void reviewCertificate(Long certId, String action, String comment);


    // ==================== 导出操作 ====================

    /**
     * 导出学院学生证书Excel
     * 权限校验：DataScope为COLLEGE时仅能导本学院，ALL时可导任意学院
     * @param collegeId 学院ID
     * @return 导出结果（数据+文件名）
     */
    CertificateExportResult exportCollegeStudentCertificates(Long collegeId);

    /**
     * 导出学院指导教师证书Excel
     * 权限校验：DataScope为COLLEGE时仅能导本学院，ALL时可导任意学院
     * @param collegeId 学院ID
     * @return 导出结果（数据+文件名）
     */
    CertificateExportResult exportCollegeInstructorCertificates(Long collegeId);

    /**
     * 导出个人学生证书Excel（当前用户作为获奖学生）
     * 权限校验：SELF，仅能导自己的证书
     * @return 导出结果（数据+文件名）
     */
    CertificateExportResult exportMyStudentCertificates();

    /**
     * 导出个人指导教师证书Excel（当前用户作为证书持有教师）
     * 权限校验：SELF，仅能导自己的证书
     * @return 导出结果（数据+文件名）
     */
    CertificateExportResult exportMyInstructorCertificates();

    /**
     * 导出教师指导的学生证书Excel（当前用户作为指导教师的学生证书）
     * 权限校验：RELATED，仅能导自己指导的学生证书
     * @return 导出结果（数据+文件名）
     */
    CertificateExportResult exportTeacherGuidedStudentCertificates();

    /**
     * 导出全部学生证书Excel
     * 权限校验：ALL，仅超管可导全部
     * @return 导出结果（数据+文件名）
     */
    CertificateExportResult exportAllStudentCertificates();

    /**
     * 导出全部指导教师证书Excel
     * 权限校验：ALL，仅超管可导全部
     * @return 导出结果（数据+文件名）
     */
    CertificateExportResult exportAllInstructorCertificates();

    /**
     * 下载证书图片
     * 权限校验：SELF/RELATED/COLLEGE/ALL（根据角色自动判断）
     * @param certId 证书ID
     * @return 证书图片文件
     */
    File downloadCertificateImage(Long certId);

    /**
     * 下载指定学生的所有证书图片（ZIP）
     * 权限校验：SELF/RELATED/COLLEGE/ALL
     * @param studentId 学生用户ID
     * @return ZIP文件
     */
    File downloadStudentCertificateImages(Long studentId);

    /**
     * 下载指定教师的所有证书图片（ZIP，仅INSTRUCTOR类型）
     * 权限校验：SELF/COLLEGE/ALL
     * @param teacherId 教师用户ID
     * @return ZIP文件
     */
    File downloadInstructorCertificateImages(Long teacherId);

    /**
     * 下载指定学院的所有证书图片（ZIP，含学生/教师子文件夹）
     * 权限校验：COLLEGE（限本学院）/ ALL
     * @param collegeId 学院ID
     * @return ZIP文件
     */
    File downloadCollegeCertificateImages(Long collegeId);

    /**
     * 导出所有学生的证书图片（ZIP，按学院分组）
     * 权限校验：ALL，仅超管
     * @return ZIP文件
     */
    File downloadAllStudentCertificateImages();

    /**
     * 导出所有教师的证书图片（ZIP，按学院分组）
     * 权限校验：ALL，仅超管
     * @return ZIP文件
     */
    File downloadAllTeacherCertificateImages();

    // ==================== 统计操作 ====================

    /**
     * 统计当前教师的INSTRUCTOR类型证书（total + 按状态分类）
     * @return 统计结果
     */
    CertificateStatsVO getMyInstructorCertStats();

    /**
     * 统计当前教师指导过的学生数（distinct，限APPROVED/REISSUED）
     * @return 学生数量
     */
    Long getMyGuidedStudentCount();

    /**
     * 统计当前学生的STUDENT类型证书（total + 按状态分类）
     * @return 统计结果
     */
    CertificateStatsVO getMyStudentCertStats();

    /**
     * 统计学院证书（按类型和状态分类）
     * @param collegeId 学院ID
     * @param certType 证书类型
     * @return 统计结果
     */
    CertificateStatsVO getCollegeCertStats(Long collegeId, CertType certType);

    /**
     * 统计所有证书（按类型和状态分类，超管用）
     * @param certType 证书类型
     * @return 统计结果
     */
    CertificateStatsVO getAllCertStats(CertType certType);

    /**
     * 获取30天证书趋势统计
     * - 结束日期 = 服务器当前日期 - 1 天
     * - 开始日期 = 结束日期 - 29 天（共30天）
     * - 无数据的日期返回全0
     * - SYSTEM_ADMIN 可查全部，COLLEGE_ADMIN 只查本学院
     * @param certType 证书类型（STUDENT/INSTRUCTOR/null表示全部）
     * @return 趋势数据
     */
    CertificateTrendResponseVO getCertificateTrend(CertType certType);

    /**
     * 获取证书状态分布统计
     * - 已通过 = APPROVED + REISSUED
     * - 待审核 = PENDING_REVIEW
     * - 已驳回 = REJECTED + REJECTED_NO_RESUBMIT
     * - 已废弃 = INVALID
     * - SYSTEM_ADMIN 可查全部，COLLEGE_ADMIN 只查本学院
     * @param certType 证书类型（STUDENT/INSTRUCTOR/null表示全部）
     * @return 状态分布（含百分比）
     */
    CertificateStatusDistributionVO getCertificateStatusDistribution(CertType certType);

    /**
     * 获取证书学院分布统计（仅超管）
     * - 有效证书 = APPROVED + REISSUED
     * - 返回所有学院，无有效证书的学院数量为0
     * @return 各学院有效证书数量分布
     */
    CollegeDistributionResponseVO getCollegeDistribution();

    /**
     * 查询教师自己持有的INSTRUCTOR类型证书状态分布（个人）
     * @return 状态分布（含百分比）
     */
    CertificateStatusDistributionVO getMyInstructorCertStatsWithPercent();

    /**
     * 查询学生自己的STUDENT类型证书状态分布（个人）
     * @return 状态分布（含百分比）
     */
    CertificateStatusDistributionVO getMyStudentCertStatsWithPercent();

}
