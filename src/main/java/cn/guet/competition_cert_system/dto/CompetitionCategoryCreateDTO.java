package cn.guet.competition_cert_system.dto;

import lombok.Data;

@Data
public class CompetitionCategoryCreateDTO {
    private String categoryName;
    private String description;
    private Long eventId;
}
