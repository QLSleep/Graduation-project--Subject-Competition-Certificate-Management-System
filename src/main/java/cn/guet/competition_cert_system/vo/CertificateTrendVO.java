package cn.guet.competition_cert_system.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 单日证书趋势数据 VO
 */
@Data
@NoArgsConstructor
public class CertificateTrendVO {

    /** 日期 */
    private LocalDate date;

    /** 当日总证书数 */
    private Long total;

    /** 草稿未提交 */
    private Long draft;

    /** 待审核 */
    private Long pendingReview;

    /** 已驳回（可重提） */
    private Long rejected;

    /** 已驳回（不可重提） */
    private Long rejectedNoResubmit;

    /** 已通过 */
    private Long approved;

    /** 已补发 */
    private Long reissued;

    /** 已作废 */
    private Long invalid;

    public CertificateTrendVO(LocalDate date) {
        this.date = date;
        this.total = 0L;
        this.draft = 0L;
        this.pendingReview = 0L;
        this.rejected = 0L;
        this.rejectedNoResubmit = 0L;
        this.approved = 0L;
        this.reissued = 0L;
        this.invalid = 0L;
    }

    /**
     * 根据状态设置数量
     */
    public void setCountByStatus(String status, Long count) {
        if (status == null || count == null) {
            return;
        }
        switch (status) {
            case "DRAFT" -> this.draft = count;
            case "PENDING_REVIEW" -> this.pendingReview = count;
            case "REJECTED" -> this.rejected = count;
            case "REJECTED_NO_RESUBMIT" -> this.rejectedNoResubmit = count;
            case "APPROVED" -> this.approved = count;
            case "REISSUED" -> this.reissued = count;
            case "INVALID" -> this.invalid = count;
        }
    }

    /**
     * 计算总数
     */
    public void calcTotal() {
        this.total = this.draft + this.pendingReview + this.rejected
                   + this.rejectedNoResubmit + this.approved + this.reissued + this.invalid;
    }
}
