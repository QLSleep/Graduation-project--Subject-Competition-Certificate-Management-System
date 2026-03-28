package cn.guet.competition_cert_system.service;

import cn.guet.competition_cert_system.common.CertPermission;
import java.util.List;

public interface CertificateService {

    // ==================== 查询操作 ====================

    /**
     * 查看指定单个用户证书列表
     * @param userId 用户ID
     * @param permission 权限级别
     * @return 根据权限返回完整VO(ALL/COLLEGE)或精简VO(RELATED/SELF)
     */
    List<Object> getUserCertificates(Long userId, CertPermission permission);

    /**
     * 查看指定个人单张证书详情
     * @param certId 证书ID
     * @param permission 权限级别
     * @return 根据权限返回完整VO或精简VO
     */
    Object getCertificateDetail(Long certId, CertPermission permission);

    /** 查看指定教师所指导的学生的证书 */
    void getStudentCertificatesByTeacher();

    /** 查看指定学院所有证书 */
    void getCollegeCertificates();

    /** 查看所有证书 */
    void getAllCertificates();

    /** 查询待审核证书列表 */
    void getPendingReviewCertificates();


    // ==================== 创建操作 ====================
    
    /** 提交新证书申请 */
    void submitCertificateApplication();


    // ==================== 编辑操作 ====================
    
    /** 编辑指定用户的指定证书 */
    void updateCertificate();


    // ==================== 删除操作 ====================
    
    /** 删除指定用户的指定证书 */
    void deleteCertificate();


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

