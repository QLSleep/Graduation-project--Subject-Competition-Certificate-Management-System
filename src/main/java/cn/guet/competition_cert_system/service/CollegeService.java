package cn.guet.competition_cert_system.service;

import cn.guet.competition_cert_system.dto.CollegeCreateDTO;
import cn.guet.competition_cert_system.dto.UpdateCollegeDTO;
import cn.guet.competition_cert_system.vo.CollegeSimpleVO;

import java.util.List;

public interface CollegeService {
    void createCollege(CollegeCreateDTO dto);

    void updateCollege(Long collegeId, UpdateCollegeDTO dto);

    List<CollegeSimpleVO> listAllColleges();
}
