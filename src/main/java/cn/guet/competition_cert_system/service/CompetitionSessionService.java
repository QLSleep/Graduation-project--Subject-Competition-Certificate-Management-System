package cn.guet.competition_cert_system.service;

import cn.guet.competition_cert_system.dto.CompetitionSessionCreateDTO;
import cn.guet.competition_cert_system.vo.CompetitionSessionSimpleVO;

import java.util.List;

public interface CompetitionSessionService {

    /**
     * 查询赛届列表（下拉用）
     * @return 赛届简单列表
     */
    List<CompetitionSessionSimpleVO> listSessions();

    /**
     * 新建赛届
     * @param dto 赛届信息
     * @return 新建赛届ID
     */
    Long createSession(CompetitionSessionCreateDTO dto);
}
