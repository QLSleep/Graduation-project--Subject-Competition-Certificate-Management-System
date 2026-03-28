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
@TableName("college")
public class College {

  @TableId
  private Long id;

  private String collegeName;

  private String collegeCode;

  private String description;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
