package cn.guet.competition_cert_system.vo;

import lombok.Data;

@Data
public class CompetitionSessionSimpleVO {
    private Long id;
    private String sessionName;
    private Integer year;
    private Long eventId;
    private String eventName;
}
