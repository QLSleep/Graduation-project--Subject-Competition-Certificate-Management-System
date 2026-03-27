package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.CertificateInstructor;
import org.apache.ibatis.annotations.Mapper;

/**
 * CertificateInstructor 数据访问层接口
 * 基于 MyBatis-Plus 的Mapper接口
 */
@Mapper
public interface CertificateInstructorMapper extends BaseMapper<CertificateInstructor> {

}
