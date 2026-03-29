package cn.guet.competition_cert_system.context;

import cn.guet.competition_cert_system.common.DataScope;
import java.util.List;

/**
 * 当前请求用户上下文
 * 使用 ThreadLocal 存储，一个请求存一份数据，互不干扰
 */
public class AuthContext {

    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();
    private static final ThreadLocal<String> realName = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> roles = new ThreadLocal<>();
    private static final ThreadLocal<Long> collegeId = new ThreadLocal<>();
    private static final ThreadLocal<DataScope> dataScope = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> permissions = new ThreadLocal<>();

    // ========== 存值 ==========

    public static void setUserId(Long id) {
        userId.set(id);
    }

    public static void setUsername(String name) {
        username.set(name);
    }

    public static void setRealName(String name) {
        realName.set(name);
    }

    public static void setRoles(List<String> roleList) {
        roles.set(roleList);
    }

    public static void setCollegeId(Long id) {
        collegeId.set(id);
    }

    public static void setDataScope(DataScope scope) {
        dataScope.set(scope);
    }

    public static void setPermissions(List<String> permList) {
        permissions.set(permList);
    }

    // ========== 取值 ==========

    public static Long getUserId() {
        return userId.get();
    }

    public static String getUsername() {
        return username.get();
    }

    public static String getRealName() {
        return realName.get();
    }

    public static List<String> getRoles() {
        return roles.get();
    }

    public static Long getCollegeId() {
        return collegeId.get();
    }

    public static DataScope getDataScope() {
        return dataScope.get();
    }

    public static List<String> getPermissions() {
        return permissions.get();
    }

    // ========== 清除（请求结束时调用） ==========

    public static void clear() {
        userId.remove();
        username.remove();
        realName.remove();
        roles.remove();
        collegeId.remove();
        dataScope.remove();
        permissions.remove();
    }
}
