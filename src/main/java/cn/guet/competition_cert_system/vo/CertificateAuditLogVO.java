package cn.guet.competition_cert_system.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书审核历史VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateAuditLogVO {

    /** 审核记录ID */
    private Long id;

    /** 证书ID */
    private Long certificateId;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人姓名 */
    private String operatorName;

    /** 操作类型 */
    private String action;

    /** 操作前状态 */
    private String beforeStatus;

    /** 操作后状态 */
    private String afterStatus;

    /** 备注/驳回原因 */
    private String comment;

    /** 操作时间 */
    private LocalDateTime operatedAt;
}
