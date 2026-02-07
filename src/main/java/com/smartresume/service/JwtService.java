// src/main/java/com/smartresume/service/JwtService.java
package com.smartresume.service;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Date;

/**
 * JWT 服务接口
 * 定义 JWT 令牌的生成、验证、解析等核心功能
 */
public interface JwtService {

    /**
     * 生成 JWT 令牌
     * @param user 用户对象（实现 UserDetails）
     * @return JWT 字符串
     */
    String generateToken(UserDetails user);

    /**
     * 验证 JWT 令牌是否有效
     * @param token JWT 字符串
     * @param userDetails 用户详情
     * @return 是否有效
     */
    boolean validateToken(String token, UserDetails userDetails);

    /**
     * 从令牌中提取用户名
     * @param token JWT 字符串
     * @return 用户名
     */
    String extractUsername(String token);

    /**
     * 获取令牌过期时间
     * @param token JWT 字符串
     * @return 过期时间
     */
    Date extractExpiration(String token);
}