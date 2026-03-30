package cn.guet.competition_cert_system.mapper;

import cn.guet.competition_cert_system.vo.CompetitionSessionSimpleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.CompetitionSession;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CompetitionSessionMapper extends BaseMapper<CompetitionSession> {

    List<CompetitionSessionSimpleVO> selectSessionList();
}
