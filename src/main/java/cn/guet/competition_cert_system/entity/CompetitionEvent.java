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
@TableName("competition_event")
public class CompetitionEvent {
  @TableId
  private Long id;

  private String eventName;

  private String abbreviation; // 赛事缩写

  private String level;

  private String sponsor; // 主办方

  private String organizer;// 承办方

  private String description;

  private Instant createdAt;

  private Instant updatedAt;
}
