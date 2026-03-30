package cn.guet.competition_cert_system.mapper;

import cn.guet.competition_cert_system.vo.SubjectGroupSimpleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.SubjectGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubjectGroupMapper extends BaseMapper<SubjectGroup> {

    List<SubjectGroupSimpleVO> selectGroupList();
}
