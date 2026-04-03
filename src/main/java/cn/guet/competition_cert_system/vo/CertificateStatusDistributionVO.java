package cn.guet.competition_cert_system.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书状态分布统计 VO
 */
@Data
@NoArgsConstructor
public class CertificateStatusDistributionVO {

    /** 证书总数 */
    private Long total;

    /** 已通过数量（含补签） */
    private Long approved;

    /** 已通过百分比 */
    private Integer approvedPercent;

    /** 待审核数量 */
    private Long pendingReview;

    /** 待审核百分比 */
    private Integer pendingReviewPercent;

    /** 已驳回数量（含可重提和不可重提） */
    private Long rejected;

    /** 已驳回百分比 */
    private Integer rejectedPercent;

    /** 已废弃数量 */
    private Long invalid;

    /** 已废弃百分比 */
    private Integer invalidPercent;

    /**
     * 计算百分比（整数除法，自动取整）
     */
    private static Integer calcPercent(Long count, Long total) {
        if (total == null || total == 0) {
            return 0;
        }
        return (int) (count * 100 / total);
    }

    /**
     * 从 CertificateStatsVO 构建分布VO
     * @param stats 已聚合好的证书状态统计
     * @return 分布VO（含百分比）
     */
    public static CertificateStatusDistributionVO fromStatsVO(CertificateStatsVO stats) {
        CertificateStatusDistributionVO vo = new CertificateStatusDistributionVO();
        vo.total = stats.getTotal();
        vo.approved = stats.getApproved();
        vo.pendingReview = stats.getPendingReview();
        vo.rejected = stats.getRejected() + stats.getRejectedNoResubmit();
        vo.invalid = stats.getInvalid();

        vo.approvedPercent = calcPercent(vo.approved, vo.total);
        vo.pendingReviewPercent = calcPercent(vo.pendingReview, vo.total);
        vo.rejectedPercent = calcPercent(vo.rejected, vo.total);
        vo.invalidPercent = calcPercent(vo.invalid, vo.total);

        return vo;
    }
}
