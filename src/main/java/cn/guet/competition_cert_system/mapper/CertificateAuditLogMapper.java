package cn.guet.competition_cert_system.mapper;

import cn.guet.competition_cert_system.entity.CertificateAuditLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CertificateAuditLogMapper extends BaseMapper<CertificateAuditLog> {

    /**
     * 根据证书ID查询审核日志列表
     * @param certificateId 证书ID
     * @return 审核日志列表
     */
    List<CertificateAuditLog> selectByCertificateId(Long certificateId);
}
