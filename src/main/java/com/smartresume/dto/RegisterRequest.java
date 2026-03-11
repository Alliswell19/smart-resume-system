package com.smartresume.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 15, message = "用户名长度3-15位")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "只能包含字母和数字")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码至少8位")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    // ✅ 可选：添加验证逻辑（确保两次密码一致）
    public boolean isPasswordConfirmed() {
        return password != null && confirmPassword != null && password.equals(confirmPassword);
    }
}