package cn.guet.competition_cert_system.service;

import cn.guet.competition_cert_system.dto.CompetitionCategoryCreateDTO;
import cn.guet.competition_cert_system.vo.CompetitionCategorySimpleVO;

import java.util.List;

public interface CompetitionCategoryService {

    /**
     * 查询赛类列表（下拉用）
     * @param eventId 赛事ID，可选
     * @return 赛类简单列表
     */
    List<CompetitionCategorySimpleVO> listCategories(Long eventId);

    /**
     * 新建赛类
     * @param dto 赛类信息
     * @return 新建赛类ID
     */
    Long createCategory(CompetitionCategoryCreateDTO dto);
}
