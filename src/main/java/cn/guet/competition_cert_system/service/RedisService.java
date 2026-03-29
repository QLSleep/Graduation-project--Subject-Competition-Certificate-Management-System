package cn.guet.competition_cert_system.service;

/**
 * Redis 操作接口
 */
public interface RedisService {

    /**
     * 存储 RefreshToken
     * @param userId 用户ID
     * @param refreshToken refreshToken值
     * @param expirationSeconds 过期时间（秒）
     */
    void setRefreshToken(Long userId, String refreshToken, long expirationSeconds);

    /**
     * 获取 RefreshToken
     * @param userId 用户ID
     * @return refreshToken值，不存在返回null
     */
    String getRefreshToken(Long userId);

    /**
     * 删除 RefreshToken（登出时调用）
     * @param userId 用户ID
     */
    void deleteRefreshToken(Long userId);

    /**
     * 检查 RefreshToken 是否存在
     * @param userId 用户ID
     * @param refreshToken 要校验的token值
     * @return 是否匹配
     */
    boolean validateRefreshToken(Long userId, String refreshToken);

    /**
     * 存储邮箱验证码
     * @param email 邮箱
     * @param code 验证码
     * @param expirationSeconds 过期时间（秒）
     */
    void setEmailCode(String email, String code, long expirationSeconds);

    /**
     * 获取邮箱验证码
     * @param email 邮箱
     * @return 验证码，不存在或过期返回null
     */
    String getEmailCode(String email);

    /**
     * 删除邮箱验证码（验证成功后删除）
     * @param email 邮箱
     */
    void deleteEmailCode(String email);
}
