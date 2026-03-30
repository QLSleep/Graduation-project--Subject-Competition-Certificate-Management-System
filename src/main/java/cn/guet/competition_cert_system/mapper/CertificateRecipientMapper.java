package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.CertificateRecipient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CertificateRecipientMapper extends BaseMapper<CertificateRecipient> {

    @Select("SELECT EXISTS(SELECT 1 FROM certificate_recipient WHERE certificate_id = #{certId} AND user_id = #{userId})")
    Boolean isRecipient(Long certId, Long userId);
}
