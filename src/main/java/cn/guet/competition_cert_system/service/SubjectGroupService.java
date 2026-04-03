package cn.guet.competition_cert_system.service;

import cn.guet.competition_cert_system.dto.SubjectGroupCreateDTO;
import cn.guet.competition_cert_system.vo.SubjectGroupSimpleVO;

import java.util.List;

public interface SubjectGroupService {

    /**
     * 查询科目组别列表（下拉用）
     * @param eventId 赛事ID，可选
     * @return 科目组别简单列表
     */
    List<SubjectGroupSimpleVO> listGroups(Long eventId);

    /**
     * 新建科目组别
     * @param dto 科目组别信息
     * @return 新建科目组别ID
     */
    Long createGroup(SubjectGroupCreateDTO dto);
}
