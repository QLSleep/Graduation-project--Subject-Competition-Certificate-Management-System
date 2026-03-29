package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.CertificateInstructor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CertificateInstructorMapper extends BaseMapper<CertificateInstructor> {

    @Select("SELECT EXISTS(SELECT 1 FROM certificate_instructor WHERE certificate_id = #{certId} AND instructor_user_id = #{userId})")
    Boolean isInstructor(Long certId, Long userId);
}
