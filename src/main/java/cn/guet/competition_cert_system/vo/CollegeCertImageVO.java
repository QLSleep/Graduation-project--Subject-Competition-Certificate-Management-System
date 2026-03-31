package cn.guet.competition_cert_system.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CollegeCertImageVO {

    /** 图片资源路径 */
    private String resourceKey;

    /** 学院ID */
    private Long collegeId;

    /** 学院名称 */
    private String collegeName;
}
