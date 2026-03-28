package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.CompetitionCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * CompetitionCategory 数据访问层接口
 */
@Mapper
public interface CompetitionCategoryMapper extends BaseMapper<CompetitionCategory> {

}
