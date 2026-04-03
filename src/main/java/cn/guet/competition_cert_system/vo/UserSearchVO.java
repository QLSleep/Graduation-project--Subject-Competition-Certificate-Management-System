package cn.guet.competition_cert_system.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSearchVO {
    /** 用户ID */
    private Long id;

    /** 真实姓名 */
    private String realName;

    /** 学号（学生用） */
    private String studentId;

    /** 工号（教师用） */
    private String teacherId;
}
