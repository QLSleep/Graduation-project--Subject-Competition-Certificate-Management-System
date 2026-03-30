package cn.guet.competition_cert_system.vo;

import lombok.Data;

@Data
public class CompetitionCategorySimpleVO {
    private Long id;
    private String categoryName;
    private String description;
    private Long eventId;
}
