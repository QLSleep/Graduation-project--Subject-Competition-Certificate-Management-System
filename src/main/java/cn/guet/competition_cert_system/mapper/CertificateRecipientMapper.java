package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.CertificateRecipient;
import org.apache.ibatis.annotations.Mapper;

/**
 * CertificateRecipient 数据访问层接口
 * 基于 MyBatis-Plus 的Mapper接口
 */
@Mapper
public interface CertificateRecipientMapper extends BaseMapper<CertificateRecipient> {

}
