package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.Certificate;
import cn.guet.competition_cert_system.vo.CertificateExportVO;
import cn.guet.competition_cert_system.vo.CertificateSimpleVO;
import cn.guet.competition_cert_system.vo.UserCertificateDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * Certificate 数据访问层接口
 * 基于 MyBatis-Plus 的Mapper接口，包含复杂查询能力
 */
@Mapper
public interface CertificateMapper extends BaseMapper<Certificate> {

  /**
   * 查看指定单个用户证书列表（精简字段，列表用）
   * @param userId 用户ID
   * @return 返回 {@link CertificateSimpleVO} 列表
   */
  List<CertificateSimpleVO> getUserCertificates(Long userId);

  /**
   * 查看指定证书详情（完整字段，详情用）
   * @param certId 证书ID
   * @return 返回 {@link UserCertificateDetailVO}
   */
  UserCertificateDetailVO getCertificateDetailById(Long certId);

  /**
   * 查看指定教师所指导的学生的证书列表（精简字段，列表用）
   * @param teacherUserId 教师用户ID
   * @return 返回 {@link CertificateSimpleVO} 列表
   */
  List<CertificateSimpleVO> getStudentCertificatesByTeacher(Long teacherUserId);

  /**
   * 查看指定学院所有证书（精简字段，列表用）
   * - STUDENT 类型：按证书聚合（多个学生）
   * - INSTRUCTOR 类型：不聚合，每个教师单独一条记录
   * @param collegeId 学院ID
   * @return 返回 {@link CertificateSimpleVO} 列表
   */
  List<CertificateSimpleVO> getCollegeCertificates(Long collegeId);

  /**
   * 查看所有证书（仅超管可访问 ALL 权限，精简字段，列表用）
   * - STUDENT 类型：按证书聚合（多个学生）
   * - INSTRUCTOR 类型：不聚合，每个教师单独一条记录
   * @return 返回 {@link CertificateSimpleVO} 列表
   */
  List<CertificateSimpleVO> getAllCertificates();

  /**
   * 查看待审核证书列表（审核员/管理员用，精简字段，列表用）
   * @param collegeId 学院ID（超管传null查全部）
   * @return 返回 {@link CertificateSimpleVO} 列表
   */
  List<CertificateSimpleVO> getPendingReviewCertificates(Long collegeId);

  // ==================== 提交证书用 ====================

  /**
   * 检查证书编号是否已存在
   * @param certNumber 证书编号
   * @return 是否存在
   */
  Boolean existsCertNumberByCertNumber(String certNumber);

  /**
   * 插入证书记录
   * @param certificate 证书实体
   */
  void insertCertificate(Certificate certificate);

  // ==================== 编辑/删除证书用 ====================

  /**
   * 更新证书主表字段（不包括关联表，关联表需单独处理）
   * @param certificate 证书实体
   */
  void updateCertificate(Certificate certificate);

  /**
   * 删除证书关联的学生获得者
   * @param certId 证书ID
   */
  void deleteRecipientsByCertId(Long certId);

  /**
   * 删除证书关联的指导教师
   * @param certId 证书ID
   */
  void deleteInstructorsByCertId(Long certId);

  // ==================== 导出证书用 ====================

  /**
   * 导出学院学生证书（审核通过/补发）
   * @param collegeId 学院ID
   * @return 导出VO列表
   */
  List<CertificateExportVO> getCollegeStudentCertificatesForExport(Long collegeId);

  /**
   * 导出学院指导教师证书（审核通过/补发）
   * @param collegeId 学院ID
   * @return 导出VO列表
   */
  List<CertificateExportVO> getCollegeInstructorCertificatesForExport(Long collegeId);

  /**
   * 导出个人学生证书（当前用户作为获奖学生）
   * @param userId 当前用户ID
   * @return 导出VO列表
   */
  List<CertificateExportVO> getMyStudentCertificatesForExport(Long userId);

  /**
   * 导出个人指导教师证书（当前用户作为证书持有教师）
   * @param userId 当前用户ID
   * @return 导出VO列表
   */
  List<CertificateExportVO> getMyInstructorCertificatesForExport(Long userId);

  /**
   * 导出教师指导的学生证书（当前用户作为 instructor_id）
   * @param teacherUserId 教师用户ID
   * @return 导出VO列表
   */
  List<CertificateExportVO> getTeacherGuidedStudentCertificatesForExport(Long teacherUserId);

  /**
   * 导出全部学生证书（审核通过/补发）
   * @return 导出VO列表
   */
  List<CertificateExportVO> getAllStudentCertificatesForExport();

  /**
   * 导出全部指导教师证书（审核通过/补发）
   * @return 导出VO列表
   */
  List<CertificateExportVO> getAllInstructorCertificatesForExport();

  /**
   * 获取指定学生的证书图片resourceKey列表（仅STUDENT类型，审核通过/补发）
   * @param userId 学生用户ID
   * @return resourceKey列表
   */
  List<String> getStudentCertificateImageKeys(Long userId);

  /**
   * 获取指定教师的证书图片resourceKey列表（仅INSTRUCTOR类型，审核通过/补发）
   * @param userId 教师用户ID
   * @return resourceKey列表
   */
  List<String> getInstructorCertificateImageKeys(Long userId);

  /**
   * 获取学院所有学生证书图片的resourceKey
   * @param collegeId 学院ID
   * @return resourceKey列表
   */
  List<String> getCollegeStudentCertificateImageKeys(Long collegeId);

  /**
   * 获取学院所有教师证书图片的resourceKey
   * @param collegeId 学院ID
   * @return resourceKey列表
   */
  List<String> getCollegeInstructorCertificateImageKeys(Long collegeId);

  /**
   * 获取所有学生的证书图片（含college信息，用于按学院分组）
   * @return CollegeCertImageVO列表
   */
  List<cn.guet.competition_cert_system.vo.CollegeCertImageVO> getAllStudentCertificateImages();

  /**
   * 获取所有教师的证书图片（含college信息，用于按学院分组）
   * @return CollegeCertImageVO列表
   */
  List<cn.guet.competition_cert_system.vo.CollegeCertImageVO> getAllInstructorCertificateImages();

  // ==================== 统计用 ====================

  /**
   * 统计教师持有的INSTRUCTOR类型证书（按状态分组）
   * @param teacherUserId 教师用户ID
   * @return List<Map<String, Object>>，每条记录含 status 和 count
   */
  List<Map<String, Object>> countInstructorCertsByTeacher(Long teacherUserId);

  /**
   * 统计教师指导过的学生数（distinct，限APPROVED/REISSUED）
   * @param teacherUserId 教师用户ID
   * @return 学生数量
   */
  Long countDistinctStudentsByTeacher(Long teacherUserId);

  /**
   * 统计学生持有的STUDENT类型证书（按状态分组）
   * @param studentUserId 学生用户ID
   * @return List<Map<String, Object>>，每条记录含 status 和 count
   */
  List<Map<String, Object>> countStudentCertsByStudent(Long studentUserId);

  /**
   * 统计学院证书（按类型和状态分组）
   * @param collegeId 学院ID
   * @param certType 证书类型（STUDENT/INSTRUCTOR）
   * @return List<Map<String, Object>>，每条记录含 status 和 count
   */
  List<Map<String, Object>> countCertsByCollege(@Param("collegeId") Long collegeId, @Param("certType") String certType);

  /**
   * 统计所有证书（按类型和状态分组，超管用）
   * @param certType 证书类型（STUDENT/INSTRUCTOR）
   * @return List<Map<String, Object>>，每条记录含 status 和 count
   */
  List<Map<String, Object>> countCertsByType(@Param("certType") String certType);
}
