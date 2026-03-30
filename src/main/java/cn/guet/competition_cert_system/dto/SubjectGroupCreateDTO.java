package cn.guet.competition_cert_system.dto;

import lombok.Data;

@Data
public class SubjectGroupCreateDTO {
    private String groupName;
    private String description;
    private Long eventId;
}
