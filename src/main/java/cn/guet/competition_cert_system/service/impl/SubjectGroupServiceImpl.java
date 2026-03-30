package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.dto.SubjectGroupCreateDTO;
import cn.guet.competition_cert_system.entity.SubjectGroup;
import cn.guet.competition_cert_system.mapper.SubjectGroupMapper;
import cn.guet.competition_cert_system.service.SubjectGroupService;
import cn.guet.competition_cert_system.vo.SubjectGroupSimpleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectGroupServiceImpl implements SubjectGroupService {

    private final SubjectGroupMapper subjectGroupMapper;

    @Override
    public List<SubjectGroupSimpleVO> listGroups() {
        return subjectGroupMapper.selectGroupList();
    }

    @Override
    public Long createGroup(SubjectGroupCreateDTO dto) {
        SubjectGroup group = new SubjectGroup();
        group.setGroupName(dto.getGroupName());
        group.setDescription(dto.getDescription());
        group.setEventId(dto.getEventId());
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());
        subjectGroupMapper.insert(group);
        return group.getId();
    }
}
