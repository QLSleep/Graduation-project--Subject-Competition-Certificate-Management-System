package cn.guet.competition_cert_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.guet.competition_cert_system.entity.UserRole;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    @Select("SELECT r.role_name FROM sys_user_role ur " +
            "JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectRoleNamesByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO sys_user_role (user_id, role_id, created_at) VALUES (#{userId}, #{roleId}, NOW())")
    void insert(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
