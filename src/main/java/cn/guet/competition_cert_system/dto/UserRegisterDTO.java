package cn.guet.competition_cert_system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegisterDTO {
    private String type;
    private String username;
    private String password;
    private String confirmPassword;
    private String realName;
    private String email;
    private String phone;
    private Long collegeId;
    private String studentId;
    private String teacherId;
    private String verifyCode;
}
