package cn.guet.competition_cert_system.service;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 包含 accessToken 和 refreshToken
     */
    LoginResult login(String username, String password);

    /**
     * 刷新 AccessToken
     * @param refreshToken refreshToken
     * @return 新的 accessToken（如果 refreshToken 有效）
     */
    String refreshAccessToken(String refreshToken);

    /**
     * 登出
     * @param userId 用户ID
     */
    void logout(Long userId);

    /**
     * 发送注册验证码
     * @param email 邮箱地址
     */
    void sendRegisterCode(String email);

    /**
     * 发送找回密码验证码
     * @param username 用户名
     * @param email 邮箱地址
     */
    void sendResetCode(String username, String email);

    /**
     * 重置密码
     * @param username 用户名
     * @param email 邮箱地址
     * @param code 验证码
     * @param newPassword 新密码
     */
    void resetPassword(String username, String email, String code, String newPassword);

    /**
     * 发送修改邮箱验证码到当前邮箱
     */
    void sendUpdateEmailCode();

    /**
     * 校验验证码并更新邮箱
     * @param code 验证码
     * @param newEmail 新邮箱
     */
    void updateEmail(String code, String newEmail);

    // ==================== 内部类 ====================

    /**
     * 登录结果
     */
    class LoginResult {
        private String accessToken;
        private String refreshToken;

        public LoginResult(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}
