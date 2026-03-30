package cn.guet.competition_cert_system.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("subject_group")
public class SubjectGroup {
  @TableId(type = IdType.AUTO)
  private Long id;

  private Long eventId;

  private String groupName;

  private String description;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
