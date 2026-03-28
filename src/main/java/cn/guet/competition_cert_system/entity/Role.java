package cn.guet.competition_cert_system.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_role")
public class Role {
  @TableId
  private Long id;

  private String roleName;

  private String roleDescription;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
