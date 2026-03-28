package cn.guet.competition_cert_system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书获得者（学生）信息VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateStudentVO {

    /** 真实姓名 */
    private String realName;

    /** 学生号 */
    private String studentId;
}