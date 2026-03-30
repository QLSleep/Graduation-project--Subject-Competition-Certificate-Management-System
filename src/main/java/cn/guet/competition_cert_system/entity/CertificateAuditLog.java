package cn.guet.competition_cert_system.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书审核记录表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("certificate_audit_log")
public class CertificateAuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 证书ID */
    private Long certificateId;

    /** 操作人ID */
    private Long operatorId;

    /**
     * 操作类型
     * SUBMIT: 提交
     * REJECT: 驳回
     * APPROVE: 通过
     * REISSUE: 补发
     * INVALID: 作废
     */
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
