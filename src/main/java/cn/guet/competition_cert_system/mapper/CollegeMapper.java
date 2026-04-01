package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.College;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CollegeMapper extends BaseMapper<College> {

    @Select("SELECT college_name FROM college WHERE id = #{id}")
    String selectNameById(Long id);

    @Select("SELECT * FROM college WHERE college_code = #{collegeCode}")
    College findByCollegeCode(@Param("collegeCode") String collegeCode);

    @Select("SELECT * FROM college WHERE college_name = #{collegeName}")
    College findByCollegeName(@Param("collegeName") String collegeName);
}
