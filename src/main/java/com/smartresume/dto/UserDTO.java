// src/main/java/com/yourproject/dto/UserDTO.java
package com.smartresume.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String email;
    private String role; // 角色：Admin, User 等
    // 可选：头像、手机号等
}