package cn.guet.competition_cert_system.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书统计结果 VO
 */
@Data
@NoArgsConstructor
public class CertificateStatsVO {

    /** 全部证书数量 */
    private Long total;

    /** 已通过（APPROVED + REISSUED） */
    private Long approved;

    /** 待审核 */
    private Long pendingReview;

    /** 已驳回（可重新提交） */
    private Long rejected;

    /** 已驳回（禁止重新提交） */
    private Long rejectedNoResubmit;

    /** 已作废 */
    private Long invalid;

    /**
     * 将 (status → count) 的 Map 列表聚合为 VO
     */
    public static CertificateStatsVO fromStatusCountList(java.util.List<java.util.Map<String, Object>> list) {
        CertificateStatsVO vo = new CertificateStatsVO();
        vo.total = 0L;
        vo.approved = 0L;
        vo.pendingReview = 0L;
        vo.rejected = 0L;
        vo.rejectedNoResubmit = 0L;
        vo.invalid = 0L;

        if (list == null || list.isEmpty()) {
            return vo;
        }

        for (java.util.Map<String, Object> row : list) {
            String status = (String) row.get("status");
            Long count = ((Number) row.get("count")).longValue();
            vo.total += count;

            if ("APPROVED".equals(status) || "REISSUED".equals(status)) {
                vo.approved += count;
            } else if ("PENDING_REVIEW".equals(status)) {
                vo.pendingReview += count;
            } else if ("REJECTED".equals(status)) {
                vo.rejected += count;
            } else if ("REJECTED_NO_RESUBMIT".equals(status)) {
                vo.rejectedNoResubmit += count;
            } else if ("INVALID".equals(status)) {
                vo.invalid += count;
            }
        }
        return vo;
    }
}
