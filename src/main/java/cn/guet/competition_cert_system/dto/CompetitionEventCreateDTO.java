package cn.guet.competition_cert_system.dto;

import cn.guet.competition_cert_system.common.CompetitionLevel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompetitionEventCreateDTO {
    private String eventName;
    private String abbreviation;
    private CompetitionLevel level;
    private String sponsor;
    private String organizer;
    private String description;
}
