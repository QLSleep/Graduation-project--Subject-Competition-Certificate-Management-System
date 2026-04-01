package cn.guet.competition_cert_system.config;

import cn.guet.competition_cert_system.common.DataScope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 角色权限硬编码配置
 * key: 角色名（如 SYSTEM_ADMIN, COLLEGE_ADMIN, TEACHER, STUDENT）
 * value: UserPermission
 */
@Component
public class PermissionConfig {

    private static final Map<String, UserPermission> ROLE_PERMISSION_MAP = Map.of(
            "ROLE_SUPER_ADMIN", new UserPermission(DataScope.ALL, List.of("CREATE", "EDIT", "DELETE", "AUDIT", "REISSUE", "EXPORT", "INVALID")),
            "ROLE_COLLEGE_ADMIN", new UserPermission(DataScope.COLLEGE, List.of("CREATE", "EDIT", "AUDIT")),
            "ROLE_CERT_AUDITOR", new UserPermission(DataScope.COLLEGE, List.of("AUDIT")),
            "ROLE_TEACHER", new UserPermission(DataScope.RELATED, List.of("CREATE")),
            "ROLE_STUDENT", new UserPermission(DataScope.SELF, List.of("CREATE"))
    );

    /**
     * 根据角色获取权限配置
     */
    public UserPermission getPermissionByRole(String role) {
        return ROLE_PERMISSION_MAP.get(role);
    }

    /**
     * 用户权限
     */
    public static class UserPermission {
        /** 数据范围 */
        private DataScope dataScope;
        /** 操作权限列表 */
        private List<String> permissions;

        public UserPermission(DataScope dataScope, List<String> permissions) {
            this.dataScope = dataScope;
            this.permissions = permissions;
        }

        public DataScope getDataScope() {
            return dataScope;
        }

        public List<String> getPermissions() {
            return permissions;
        }
    }
}
