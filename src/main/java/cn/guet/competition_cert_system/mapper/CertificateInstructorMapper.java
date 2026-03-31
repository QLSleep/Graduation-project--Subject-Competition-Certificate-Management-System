package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.CertificateInstructor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CertificateInstructorMapper extends BaseMapper<CertificateInstructor> {

    @Select("SELECT EXISTS(" +
            "  SELECT 1 FROM certificate_instructor ci WHERE ci.certificate_id = #{certId} AND ci.user_id = #{userId}" +
            "  UNION ALL" +
            "  SELECT 1 FROM certificate_recipient cr WHERE cr.certificate_id = #{certId} AND cr.instructor_id = #{userId}" +
            ")")
    Boolean isInstructor(Long certId, Long userId);
}
