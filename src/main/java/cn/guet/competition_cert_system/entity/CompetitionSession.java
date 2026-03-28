package cn.guet.competition_cert_system.entity;

import java.time.LocalDateTime;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("competition_session")
public class CompetitionSession {
  
  @TableId
  private Long id;

  private Long eventId;

  private String sessionName;

  private Integer year;

  private LocalDate startDate;

  private LocalDate endDate;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
