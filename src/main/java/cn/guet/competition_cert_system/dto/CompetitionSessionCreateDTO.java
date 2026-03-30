package cn.guet.competition_cert_system.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CompetitionSessionCreateDTO {
    private String sessionName;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long eventId;
}
