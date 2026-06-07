package com.smartresume.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret:smartresumeSecretKeyWithAtLeast32BytesLongForHS512}")
    private String secret;

    @Value("${jwt.expiration:86400}")
    private Long expiration;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 生成token（包含用户ID、用户名和角色）
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        return generateToken(claims, username);
    }

    // 生成token（包含用户ID和用户名）
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return generateToken(claims, username);
    }

    // 从token中提取用户ID
    public Long getUserIdFromToken(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            Object userId = claims.get("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else if (userId instanceof Long) {
                return (Long) userId;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // 从token中提取角色
    public String getRoleFromToken(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            Object role = claims.get("role");
            return role != null ? role.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // 生成token（包含自定义claims）
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 从token中提取用户名
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从token中提取过期时间
    public Date getExpirationDateFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 提取指定claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 提取所有claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查token是否过期
    private Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    // 验证token
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = getUsernameFromToken(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // 验证token（简化版）
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}