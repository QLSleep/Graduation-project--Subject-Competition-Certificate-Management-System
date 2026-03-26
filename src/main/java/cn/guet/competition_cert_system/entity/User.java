package cn.guet.competition_cert_system.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
public class User {
  @TableId
  private Long id;

  private String username;

  private String password;

  private String realName;

  private String email;

  private String phone;

  private String studentId;

  private String teacherId;

  private Long collegeId;

  private Boolean enabled;

  private Instant createdAt;

  private Instant updatedAt;
}
