package cn.guet.competition_cert_system.service;

import cn.guet.competition_cert_system.dto.CompetitionEventCreateDTO;
import cn.guet.competition_cert_system.vo.CompetitionEventSimpleVO;

import java.util.List;

public interface CompetitionEventService {

    /**
     * 查询赛事列表（下拉用）
     * @return 赛事简单列表
     */
    List<CompetitionEventSimpleVO> listEvents();

    /**
     * 新建赛事
     * @param dto 赛事信息
     * @return 新建赛事ID
     */
    Long createEvent(CompetitionEventCreateDTO dto);
}
