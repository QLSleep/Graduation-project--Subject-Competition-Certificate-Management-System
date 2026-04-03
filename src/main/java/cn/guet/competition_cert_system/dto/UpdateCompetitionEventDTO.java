package cn.guet.competition_cert_system.dto;

import cn.guet.competition_cert_system.common.CompetitionLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCompetitionEventDTO {
    private String eventName;
    private String abbreviation;
    private CompetitionLevel level;
    private String sponsor;
    private String organizer;
    private String description;
}
