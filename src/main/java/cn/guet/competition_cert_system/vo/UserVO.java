package cn.guet.competition_cert_system.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String studentId;
    private String teacherId;
    private Long collegeId;
    private String collegeName;
    private Boolean enabled;
    private String enabledText;
    private List<String> roles;
    private LocalDateTime createdAt;
}
