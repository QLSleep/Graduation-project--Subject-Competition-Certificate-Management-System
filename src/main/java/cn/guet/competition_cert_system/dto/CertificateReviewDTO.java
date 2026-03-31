package cn.guet.competition_cert_system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书审核请求DTO
 */
@Data
@NoArgsConstructor
public class CertificateReviewDTO {

    /**
     * 审核动作
     * APPROVE: 通过
     * REJECT: 驳回-可重提
     * REJECT_NO_RESUBMIT: 驳回-不可重提
     * REISSUE: 补发
     * INVALID: 作废
     */
    private String action;

    /**
     * 备注/驳回原因/作废原因
     * REJECT / REJECT_NO_RESUBMIT / INVALID 时必填
     */
    private String comment;
}
