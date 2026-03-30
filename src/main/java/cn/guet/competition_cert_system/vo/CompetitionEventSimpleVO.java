package cn.guet.competition_cert_system.vo;

import cn.guet.competition_cert_system.common.CompetitionLevel;
import lombok.Data;

@Data
public class CompetitionEventSimpleVO {
    private Long id;
    private String eventName;
    private String abbreviation;
    private CompetitionLevel level;
}
