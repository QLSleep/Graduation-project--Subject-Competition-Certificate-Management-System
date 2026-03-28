package cn.guet.competition_cert_system.entity;

import cn.guet.competition_cert_system.common.CompetitionLevel;

import java.time.LocalDateTime;

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

  private CompetitionLevel level; // 赛事级别：校级、市级、省级、国家级、全球

  private String sponsor; // 主办方

  private String organizer;// 承办方

  private String description;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
