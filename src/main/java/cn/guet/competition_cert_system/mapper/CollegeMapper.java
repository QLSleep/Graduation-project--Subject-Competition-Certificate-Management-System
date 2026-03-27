package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.College;
import org.apache.ibatis.annotations.Mapper;

/**
 * College 数据访问层接口
 * 基于 MyBatis-Plus 的Mapper接口
 */
@Mapper
public interface CollegeMapper extends BaseMapper<College> {

}
