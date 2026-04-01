package cn.guet.competition_cert_system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CollegeCreateDTO {
    private String collegeName;
    private String collegeCode;
    private String description;
}
