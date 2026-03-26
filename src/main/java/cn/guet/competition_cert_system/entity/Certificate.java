package cn.guet.competition_cert_system.entity;

import cn.guet.competition_cert_system.common.CertStatus;
import cn.guet.competition_cert_system.common.CertType;

import java.time.Instant;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("certificate")
public class Certificate {
  @TableId
  private Long id;

  private CertType certType;

  private String certNumber;

  private Long ownerCollegeId;

  private Long createBy;

  private Long submitterUserId;

  private Long reviewerUserId;

  private Long eventId;

  private Long sessionId;

  private Long groupId;

  private String certTitle;

  private String awardLevel;

  private LocalDate issuDate;

  private String resourceKey;

  private CertStatus status;

  private String remark;

  private Instant createdAt;

  private Instant updatedAt;
}
