package cn.guet.competition_cert_system.mapper;

import cn.guet.competition_cert_system.vo.CompetitionCategorySimpleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.CompetitionCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CompetitionCategoryMapper extends BaseMapper<CompetitionCategory> {

    List<CompetitionCategorySimpleVO> selectCategoryList();
}
