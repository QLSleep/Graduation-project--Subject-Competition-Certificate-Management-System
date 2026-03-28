package cn.guet.competition_cert_system.service;

import cn.guet.competition_cert_system.common.DataScope;
import cn.guet.competition_cert_system.vo.UserCertificateDetailVO;

import java.util.List;

public interface CertificateService {

    // ==================== 查询操作 ====================

    /**
     * 查看指定单个用户证书列表
     * @param userId 用户ID
     * @param dataScope 数据可见范围
     * @return 根据范围返回完整VO(ALL/COLLEGE)或精简VO(RELATED/SELF)
     */
    List<Object> getUserCertificates(Long userId, DataScope dataScope);

    /**
     * 查看指定个人单张证书详情
     * @param certId 证书ID
     * @param dataScope 数据可见范围
     * @return 根据范围返回完整VO或精简VO
     */
    Object getCertificateDetail(Long certId, DataScope dataScope);

    /**
     * 查看指定教师所指导的学生的证书列表
     * @param teacherUserId 教师用户ID
     * @param dataScope 数据可见范围
     * @return 根据范围返回完整VO或精简VO
     */
    List<Object> getStudentCertificatesByTeacher(Long teacherUserId, DataScope dataScope);

    /**
     * 查看指定学院所有证书
     * - STUDENT 证书：按证书聚合（多个学生）
     * - INSTRUCTOR 证书：不聚合，每个教师单独一条记录
     * - 始终返回完整VO
     * 注意：权限校验在拦截器/Controller 层完成，Service 只做业务逻辑
     * @param collegeId 学院ID
     * @return 完整 UserCertificateDetailVO 列表
     */
    List<UserCertificateDetailVO> getCollegeCertificates(Long collegeId);

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

