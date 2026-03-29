package cn.guet.competition_cert_system.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    // ==================== 生成 Token ====================

    /**
     * 生成 AccessToken（包含用户信息、角色、数据范围、操作权限）
     */
    public String generateAccessToken(Long userId, String username, String realName,
            List<String> roles, Long collegeId, String dataScope, List<String> permissions) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("realName", realName)
                .claim("roles", roles)
                .claim("collegeId", collegeId)
                .claim("dataScope", dataScope)
                .claim("permissions", permissions)
                .claim("tokenType", "access")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成 RefreshToken（只包含 userId 和 tokenType）
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .claim("userId", userId)
                .claim("tokenType", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    // ==================== 验证 Token ====================

    /**
     * 验证 Token 是否有效
     */
    public Boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== 提取 Claims ====================

    /**
     * 提取完整 Payload
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ==================== 便捷提取方法 ====================

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }

    public String extractRealName(String token) {
        return extractAllClaims(token).get("realName", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) extractAllClaims(token).get("roles", List.class);
    }

    public Long extractCollegeId(String token) {
        return extractAllClaims(token).get("collegeId", Long.class);
    }

    public String extractDataScope(String token) {
        return extractAllClaims(token).get("dataScope", String.class);
    }

    public String extractTokenType(String token) {
        return extractAllClaims(token).get("tokenType", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        return (List<String>) extractAllClaims(token).get("permissions", List.class);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
