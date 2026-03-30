package cn.guet.competition_cert_system.vo;

import lombok.Data;

@Data
public class SubjectGroupSimpleVO {
    private Long id;
    private String groupName;
    private String description;
    private Long eventId;
}
