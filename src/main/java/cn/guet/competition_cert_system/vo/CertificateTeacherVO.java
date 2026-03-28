package cn.guet.competition_cert_system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书指导教师信息VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateTeacherVO {

    /** 真实姓名 */
    private String realName;

    /** 教师号 */
    private String teacherId;
}