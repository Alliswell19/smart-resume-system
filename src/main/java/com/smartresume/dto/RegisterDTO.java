package com.smartresume.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须介于3到50个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须介于6到50个字符之间")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @NotNull(message = "角色不能为空")
    @Pattern(regexp = "^(JOB_SEEKER|HR|ADMIN)$", message = "角色必须是 JOB_SEEKER、HR 或 ADMIN 之一")
    private String role;
}