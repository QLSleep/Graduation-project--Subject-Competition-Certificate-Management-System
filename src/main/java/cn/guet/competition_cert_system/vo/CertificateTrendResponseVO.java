package cn.guet.competition_cert_system.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 30天证书趋势响应 VO
 */
@Data
@NoArgsConstructor
public class CertificateTrendResponseVO {

    /** 开始日期 */
    private String startDate;

    /** 结束日期 */
    private String endDate;

    /** 每日趋势数据（固定30条，无数据的日期返回全0） */
    private List<CertificateTrendVO> dailyData;

    public CertificateTrendResponseVO(String startDate, String endDate, List<CertificateTrendVO> dailyData) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyData = dailyData;
    }
}
