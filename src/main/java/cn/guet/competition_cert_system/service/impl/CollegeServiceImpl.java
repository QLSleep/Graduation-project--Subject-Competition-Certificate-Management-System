package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.common.ResultCode;
import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.dto.CollegeCreateDTO;
import cn.guet.competition_cert_system.dto.UpdateCollegeDTO;
import cn.guet.competition_cert_system.entity.College;
import cn.guet.competition_cert_system.exception.BusinessException;
import cn.guet.competition_cert_system.mapper.CollegeMapper;
import cn.guet.competition_cert_system.service.CollegeService;
import cn.guet.competition_cert_system.vo.CollegeSimpleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollegeServiceImpl implements CollegeService {

    private final CollegeMapper collegeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCollege(CollegeCreateDTO dto) {
        // 1. 权限校验：只有超管可以创建学院
        String primaryRole = AuthContext.getRoles().get(0);
        if (!"SYSTEM_ADMIN".equals(primaryRole)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 2. 校验 collegeCode 不重复
        if (dto.getCollegeCode() != null && !dto.getCollegeCode().isBlank()) {
            if (collegeMapper.findByCollegeCode(dto.getCollegeCode()) != null) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "学院代码已存在");
            }
        }

        // 3. 校验 collegeName 不重复
        if (dto.getCollegeName() != null && !dto.getCollegeName().isBlank()) {
            if (collegeMapper.findByCollegeName(dto.getCollegeName()) != null) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "学院名称已存在");
            }
        }

        // 4. 创建学院
        College college = new College();
        college.setCollegeName(dto.getCollegeName());
        college.setCollegeCode(dto.getCollegeCode());
        college.setDescription(dto.getDescription());
        college.setCreatedAt(LocalDateTime.now());
        college.setUpdatedAt(LocalDateTime.now());
        collegeMapper.insert(college);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCollege(Long collegeId, UpdateCollegeDTO dto) {
        // 1. 权限校验：只有超管可以修改学院
        String primaryRole = AuthContext.getRoles().get(0);
        if (!"SYSTEM_ADMIN".equals(primaryRole)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 2. 查询学院是否存在
        College college = collegeMapper.selectById(collegeId);
        if (college == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 3. collegeCode 唯一性校验（排除自身）
        if (dto.getCollegeCode() != null && !dto.getCollegeCode().equals(college.getCollegeCode())) {
            if (collegeMapper.findByCollegeCode(dto.getCollegeCode()) != null) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "学院代码已存在");
            }
        }

        // 4. collegeName 唯一性校验（排除自身）
        if (dto.getCollegeName() != null && !dto.getCollegeName().equals(college.getCollegeName())) {
            if (collegeMapper.findByCollegeName(dto.getCollegeName()) != null) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "学院名称已存在");
            }
        }

        // 5. 更新字段
        if (dto.getCollegeName() != null) {
            college.setCollegeName(dto.getCollegeName());
        }
        if (dto.getCollegeCode() != null) {
            college.setCollegeCode(dto.getCollegeCode());
        }
        if (dto.getDescription() != null) {
            college.setDescription(dto.getDescription());
        }
        college.setUpdatedAt(LocalDateTime.now());
        collegeMapper.updateById(college);
    }

    @Override
    public List<CollegeSimpleVO> listAllColleges() {
        return collegeMapper.selectList(null).stream()
                .map(c -> {
                    CollegeSimpleVO vo = new CollegeSimpleVO();
                    vo.setId(c.getId());
                    vo.setCollegeName(c.getCollegeName());
                    vo.setCollegeCode(c.getCollegeCode());
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
