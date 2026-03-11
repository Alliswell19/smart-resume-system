package com.smartresume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@TableName("user")
public class User implements UserDetails {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;  // 用户名（主登录方式）

    @TableField("password")
    private String password;

    @TableField("email")
    private String email;     // 邮箱（可选，用于找回密码）

    @TableField("phone")
    private String phone;     // 手机号（可选）

    @TableField("avatar")
    private String avatar;    // 头像

    @TableField("nickname")
    private String nickname;  // 昵称

    @TableField("role")
    private String role = "USER";

    @TableField("status")
    private Integer status = 1; // 0-禁用 1-启用

    // 逻辑删除字段
    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted = false;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 最后登录时间
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    // ==================== UserDetails 接口实现 ====================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null && !role.trim().isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.trim()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == 1 && !Boolean.TRUE.equals(isDeleted);
    }
}