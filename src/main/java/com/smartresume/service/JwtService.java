package com.smartresume.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * JWT 服务接口
 */
public interface JwtService {

    /**
     * 从 Token 中提取用户名
     * @param token JWT 字符串
     * @return 用户名
     */
    String extractUsername(String token);

    /**
     * 生成 JWT Token
     * @param userDetails 用户详情
     * @return JWT 字符串
     */
    String generateToken(UserDetails userDetails);

    /**
     * 验证 JWT Token
     * @param token JWT 字符串
     * @return 是否有效
     */
    boolean validateToken(String token);
}