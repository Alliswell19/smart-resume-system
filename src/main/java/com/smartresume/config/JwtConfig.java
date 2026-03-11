// src/main/java/com/smartresume/config/JwtConfig.java
package com.smartresume.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    // ✅ 强制要求：密钥长度 ≥ 32 字节（HS256 最低要求）
    @Value("${jwt.signing-key:SmartResumeSecretKeyForHS256Algorithm2024}") 
    private String signingKey;

    @Bean(name = "jwtSigningKey") // ✅ 名称必须与 @Qualifier 匹配
    public SecretKey jwtSigningKey() {
        // 验证密钥长度（开发环境友好提示）
        if (signingKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException(
                "JWT 密钥长度不足！HS256 要求 ≥ 32 字节，当前长度: " + 
                signingKey.getBytes(StandardCharsets.UTF_8).length + " 字节"
            );
        }
        return Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
    }
}