package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

/**
 * User 数据访问层接口
 * 基于 MyBatis-Plus 的Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据学号查询用户
     */
    @Select("SELECT * FROM sys_user WHERE student_id = #{studentId}")
    User findByStudentId(@Param("studentId") String studentId);

    /**
     * 根据工号查询用户
     */
    @Select("SELECT * FROM sys_user WHERE teacher_id = #{teacherId}")
    User findByTeacherId(@Param("teacherId") String teacherId);
}
