package cn.guet.competition_cert_system.vo;

import java.time.Instant;
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
    
    /** 教师用户名 */
    private String username;
    
    /** 真实姓名 */
    private String realName;
    
    /** 邮箱 */
    private String email;
    
    /** 电话 */
    private String phone;
    
    /** 教师号 */
    private String teacherId;
    
    /** 学院名 */
    private String collegeName;
    
    /** 状态（enabled） */
    private Boolean enabled;
    
    /** 创建时间 */
    private Instant createdAt;
    
    /** 更新时间 */
    private Instant updatedAt;
}
