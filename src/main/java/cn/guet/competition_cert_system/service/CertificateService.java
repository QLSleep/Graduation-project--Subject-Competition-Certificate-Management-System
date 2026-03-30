package cn.guet.competition_cert_system.service;

import cn.guet.competition_cert_system.common.DataScope;
import cn.guet.competition_cert_system.common.CertType;
import cn.guet.competition_cert_system.dto.CertificateSubmitDTO;
import cn.guet.competition_cert_system.vo.CertificateSimpleVO;
import cn.guet.competition_cert_system.vo.UserCertificateDetailVO;

import java.util.List;

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

    /** 上传证书相关图片 */
    void uploadCertificateImage();


    // ==================== 审核操作 ====================

    /** 提交证书审核操作 */
    void reviewCertificate();


    // ==================== 导出操作 ====================

    /** 导出指定学院证书 */
    void exportCollegeCertificates();

    /** 导出所有证书 */
    void exportAllCertificates();

    /** 导出单个指定用户所有证书 */
    void exportUserCertificates();

    /** 导出多个指定用户所有证书 */
    void exportMultipleUsersCertificates();

}
