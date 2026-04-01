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

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM sys_user WHERE LOWER(email) = LOWER(#{email})")
    User selectByEmail(@Param("email") String email);

    /**
     * 查询本院所有师生（用于院管）
     */
    @Select("SELECT * FROM sys_user WHERE college_id = #{collegeId} AND id IN " +
           "(SELECT user_id FROM sys_user_role WHERE role_id IN " +
           "(SELECT id FROM sys_role WHERE role_name IN ('TEACHER', 'STUDENT')))")
    java.util.List<User> selectByCollegeIdForCollegeAdmin(@Param("collegeId") Long collegeId);

    /**
     * 查询所有非超管用户（用于超管）
     */
    @Select("SELECT * FROM sys_user WHERE id NOT IN " +
           "(SELECT user_id FROM sys_user_role WHERE role_id = " +
           "(SELECT id FROM sys_role WHERE role_name = 'SYSTEM_ADMIN'))")
    java.util.List<User> selectAllExceptSystemAdmin();
}
