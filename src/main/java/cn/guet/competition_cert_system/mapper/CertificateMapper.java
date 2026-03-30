package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.Certificate;
import cn.guet.competition_cert_system.vo.CertificateSimpleVO;
import cn.guet.competition_cert_system.vo.UserCertificateDetailVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

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

  /** 导出指定学院证书 */
  void exportCollegeCertificates();

  /** 导出所有证书 */
  void exportAllCertificates();

  /** 导出单个指定用户所有证书 */
  void exportUserCertificates();

  /** 导出多个指定用户所有证书 */
  void exportMultipleUsersCertificates();
}
