package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.Certificate;
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
   * 查看指定单个用户证书列表
   * @param userId 用户ID
   * @return 返回 {@link UserCertificateDetailVO} 列表，包含证书详情、学生获得者和指导教师信息
   */
  List<UserCertificateDetailVO> getUserCertificates(Long userId);

  /**
   * 查看指定证书详情
   * @param certId 证书ID
   * @return 返回 {@link UserCertificateDetailVO}
   */
  UserCertificateDetailVO getCertificateDetailById(Long certId);

  /**
   * 查看指定教师所指导的学生的证书列表
   * @param teacherUserId 教师用户ID
   * @return 返回 {@link UserCertificateDetailVO} 列表，包含证书详情和学生获得者信息
   */
  List<UserCertificateDetailVO> getStudentCertificatesByTeacher(Long teacherUserId);

    /**
     * 查看指定学院所有证书
     * - STUDENT 类型：按证书聚合（多个学生）
     * - INSTRUCTOR 类型：不聚合，每个教师单独一条记录
     * @param collegeId 学院ID
     * @return 返回 {@link UserCertificateDetailVO} 列表
     */
    List<UserCertificateDetailVO> getCollegeCertificates(Long collegeId);

  /** 查看所有证书 */
  void getAllCertificates();

  /** 查询待审核证书列表 */
  void getPendingReviewCertificates();

  /** 提交新证书申请 */
  void submitCertificateApplication();

  /** 编辑指定用户的指定证书 */
  void updateCertificate();

  /** 删除指定用户的指定证书 */
  void deleteCertificate();

  /** 提交证书审核操作 */
  void reviewCertificate();

  /** 导出指定学院证书 */
  void exportCollegeCertificates();

  /** 导出所有证书 */
  void exportAllCertificates();

  /** 导出单个指定用户所有证书 */
  void exportUserCertificates();

  /** 导出多个指定用户所有证书 */
  void exportMultipleUsersCertificates();
}
