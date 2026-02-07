// src/main/java/com/smartresume/dto/RegisterRequest.java
package com.smartresume.dto;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;


@Setter
@Getter
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    @JsonProperty("password")
    private String rawPassword; // ← 字段名必须是 rawPassword

    @NotBlank(message = "请确认密码")
    private String confirmPassword;

}