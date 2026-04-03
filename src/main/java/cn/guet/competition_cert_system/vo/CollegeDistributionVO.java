package cn.guet.competition_cert_system.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学院证书分布 VO
 */
@Data
@NoArgsConstructor
public class CollegeDistributionVO {

    /** 学院ID */
    private Long collegeId;

    /** 学院名称 */
    private String collegeName;

    /** 有效证书数量（APPROVED + REISSUED） */
    private Long validCertCount;
}
