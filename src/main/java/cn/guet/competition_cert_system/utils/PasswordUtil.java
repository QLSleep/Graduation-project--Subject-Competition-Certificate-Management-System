package cn.guet.competition_cert_system.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码工具类
 * 使用 SHA-256 + 盐值 加密存储
 *
 * 默认密码: 12345678
 * 加密后: 5c625b6ec350ab0ba4e0e36a41b77b3ad45b366249ecc8fb167e7ea534fba141
 */
@Component
public class PasswordUtil {

    private static String salt;

    @Value("${password.salt}")
    public void setSalt(String saltValue) {
        PasswordUtil.salt = saltValue;
    }

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 20;

    /**
     * 加密密码
     */
    public static String encrypt(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (rawPassword.length() < MIN_PASSWORD_LENGTH || rawPassword.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("密码长度需在" + MIN_PASSWORD_LENGTH + "-" + MAX_PASSWORD_LENGTH + "位之间");
        }
        return sha256(rawPassword + salt);
    }

    /**
     * 校验密码
     */
    public static boolean matches(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || encryptedPassword == null) {
            return false;
        }
        return sha256(rawPassword + salt).equals(encryptedPassword);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不存在", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
