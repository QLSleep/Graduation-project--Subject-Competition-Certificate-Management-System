package cn.guet.competition_cert_system.dto;

import cn.guet.competition_cert_system.common.CompetitionLevel;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompetitionEventCreateDTO {
    private String eventName;
    private String abbreviation;
    private CompetitionLevel level;
    private String sponsor;
    private String organizer;
    private String description;

    // 赛届信息
    private String sessionName;
    private Integer sessionYear;
    private LocalDate startDate;
    private LocalDate endDate;

    // 科目组别信息
    private String groupName;
    private String groupDescription;

    // 赛类信息
    private String categoryName;
    private String categoryDescription;
}
