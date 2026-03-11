// src/main/java/com/smartresume/dto/UserDTO.java
package com.smartresume.dto;

import java.io.Serializable;

/**
 * 用户数据传输对象 - 仅包含安全字段
 * 用于API响应，避免暴露密码等敏感信息
 */
public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String email;
    private String role;

    // 无参构造（Jackson序列化必需）
    public UserDTO() {}

    // 全参构造
    public UserDTO(Long id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getter and Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}