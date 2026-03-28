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
@TableName("certificate_instructor")
public class CertificateInstructor {
  @TableId
  private Long id;

  private Long certificateId;

  private Long userId;

  private LocalDateTime createdAt;
}
