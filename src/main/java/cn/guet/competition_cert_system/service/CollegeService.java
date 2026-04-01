package cn.guet.competition_cert_system.service;

import cn.guet.competition_cert_system.dto.CollegeCreateDTO;
import cn.guet.competition_cert_system.dto.UpdateCollegeDTO;

public interface CollegeService {
    void createCollege(CollegeCreateDTO dto);

    void updateCollege(Long collegeId, UpdateCollegeDTO dto);
}
