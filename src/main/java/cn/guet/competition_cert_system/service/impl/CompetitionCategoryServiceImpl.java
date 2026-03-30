package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.dto.CompetitionCategoryCreateDTO;
import cn.guet.competition_cert_system.entity.CompetitionCategory;
import cn.guet.competition_cert_system.mapper.CompetitionCategoryMapper;
import cn.guet.competition_cert_system.service.CompetitionCategoryService;
import cn.guet.competition_cert_system.vo.CompetitionCategorySimpleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionCategoryServiceImpl implements CompetitionCategoryService {

    private final CompetitionCategoryMapper competitionCategoryMapper;

    @Override
    public List<CompetitionCategorySimpleVO> listCategories() {
        return competitionCategoryMapper.selectCategoryList();
    }

    @Override
    public Long createCategory(CompetitionCategoryCreateDTO dto) {
        CompetitionCategory category = new CompetitionCategory();
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
        category.setEventId(dto.getEventId());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        competitionCategoryMapper.insert(category);
        return category.getId();
    }
}
