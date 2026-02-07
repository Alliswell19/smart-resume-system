// src/main/java/com/smartresume/service/impl/JwtServiceImpl.java
package com.smartresume.service.impl;

import com.smartresume.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 服务实现类
 * 使用 io.jsonwebtoken 库实现 JWT 功能
 */
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret:mySecretKey}") // 如果配置文件没有，使用默认值
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 默认 24 小时（毫秒）
    private long expiration;

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 创建 JWT 令牌
     * @param claims JWT 中的声明
     * @param subject 主题（通常是用户名）
     * @return JWT 字符串
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    /**
     * 从令牌中提取特定声明
     * @param token JWT 字符串
     * @param claimsResolver 声明解析器
     * @param <T> 声明类型
     * @return 解析后的声明值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析 JWT 令牌中的所有声明
     * @param token JWT 字符串
     * @return 声明对象
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    /**
     * 判断令牌是否过期
     * @param token JWT 字符串
     * @return 是否过期
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}