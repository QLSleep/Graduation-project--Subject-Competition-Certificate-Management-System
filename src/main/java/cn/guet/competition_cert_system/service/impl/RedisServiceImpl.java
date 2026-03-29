package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String EMAIL_CODE_PREFIX = "email:code:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    // ==================== RefreshToken ====================

    @Override
    public void setRefreshToken(Long userId, String refreshToken, long expirationSeconds) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, expirationSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String getRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }

    @Override
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        String stored = getRefreshToken(userId);
        return refreshToken.equals(stored);
    }

    // ==================== 邮箱验证码 ====================

    @Override
    public void setEmailCode(String email, String code, long expirationSeconds) {
        String key = EMAIL_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, expirationSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String getEmailCode(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteEmailCode(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        redisTemplate.delete(key);
    }
}
