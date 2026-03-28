package cn.guet.competition_cert_system.vo;

import cn.guet.competition_cert_system.common.CertStatus;
import cn.guet.competition_cert_system.common.CertType;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书精简版VO
 * 给 RELATED/SELF 权限的用户查看，不包含敏感字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateSimpleVO {

    /** 证书ID */
    private Long id;

    /** 证书类型 */
    private CertType certType;

    /** 证书号码 */
    private String certNumber;

    /** 归属学院 */
    private String collegeName;

    /** 赛事 */
    private String eventName;

    /** 赛届 */
    private String sessionName;

    /** 科目组别 */
    private String groupName;

    /** 证书标题 */
    private String certTitle;

    /** 级别 */
    private String awardLevel;

    /** 签发日期 */
    private LocalDate issuDate;

    /** 图片资源路径 */
    private String resourceKey;

    /** 状态 */
    private CertStatus status;

    /** 备注 */
    private String remark;

    /** 学生获得者列表（只显示姓名+学号） */
    private List<CertificateStudentVO> recipients;

    /** 指导教师（只显示姓名+教师号） */
    private CertificateTeacherVO instructor;
}
