package cn.guet.competition_cert_system.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 学院分布统计响应 VO
 */
@Data
@NoArgsConstructor
public class CollegeDistributionResponseVO {

    /** 全局有效证书总数 */
    private Long totalValidCerts;

    /** 各学院分布列表 */
    private List<CollegeDistributionVO> collegeList;

    public CollegeDistributionResponseVO(Long totalValidCerts, List<CollegeDistributionVO> collegeList) {
        this.totalValidCerts = totalValidCerts;
        this.collegeList = collegeList;
    }
}
