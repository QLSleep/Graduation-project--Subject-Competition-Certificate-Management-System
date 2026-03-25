package cn.guet.competition_cert_system.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.guet.competition_cert_system.common.ResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {

  // 从配置文件读取密钥
  @Value("${jwt.secret}")
  private String secret;

  // 从配置文件读取access过期时间
  @Value("${jwt.access-expiration}")
  private Long accessExpiration;

  // 从配置文件读取refresh过期时间
  @Value("${jwt.access-expiration}")
  private Long refreshExpiration;

  // TODO
  // 生成 Access Token
  public String generateAccessToken(Long userId, String username, String realName, List<String> roles, Long collegeId,
      String dataScope) {

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + accessExpiration);

    return Jwts.builder()
        // 自定义 Payload 字段
        .claim("userId", userId)
        .claim("username", username)
        .claim("realName", realName)
        .claim("roles", roles)
        .claim("collegeId", collegeId)
        .claim("dataScope", dataScope)
        // 标准字段
        .issuedAt(now)
        .expiration(expiryDate)
        // 显式指定算法
        .signWith(getSigningKey())
        .compact();
  }

  // 生成 Refresh Token
  public String generateRefreshToken(Long userId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshExpiration);

    return Jwts.builder()
        // 自定义 Payload 字段
        .claim("userId", userId)
        .claim("tokenType", "refresh")
        // 标准字段
        .issuedAt(now)
        .expiration(expiryDate)
        // 显式指定算法
        .signWith(getSigningKey())
        .compact();
  }

  // 通用验证 Token 是否有效（签名正确 + 未过期）
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
      return false; // 签名错误/过期/格式错误
    }
  }

  // 从 Token 中提取完整 Payload (Map 形式)
  public Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  // 便捷提取方法
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

  // 内部私有方法
  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }
}
