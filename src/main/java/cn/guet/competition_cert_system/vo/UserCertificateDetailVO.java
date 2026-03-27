package cn.guet.competition_cert_system.vo;

import cn.guet.competition_cert_system.common.CertStatus;
import cn.guet.competition_cert_system.common.CertType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户证书详情VO
 * 包含证书基本信息、获得者列表和指导教师信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCertificateDetailVO {
    
    /** 证书ID */
    private Long id;
    
    /** 证书类型 */
    private CertType certType;
    
    /** 证书号码 */
    private String certNumber;
    
    /** 归属学院 */
    private String collegeName;
    
    /** 创建人 */
    private String creatorName;
    
    /** 提交人 */
    private String submitterName;
    
    /** 审核人 */
    private String reviewerName;
    
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
    
    /** 创建时间 */
    private Instant createdAt;
    
    /** 更新时间 */
    private Instant updatedAt;
    
    /** 学生获得者列表 */
    private List<CertificateStudentVO> recipients;
    
    /** 指导教师 */
    private CertificateTeacherVO instructor;
}
