package com.smartresume.controller;

import com.smartresume.common.Result;
import com.smartresume.dto.LoginDTO;
import com.smartresume.dto.RegisterDTO;
import com.smartresume.entity.User;
import com.smartresume.service.UserService;
import com.smartresume.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.findByUsername(loginDTO.getUsername());
            if (user == null) {
                return Result.error("用户名或密码错误");
            }

            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return Result.error("用户名或密码错误");
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(user.getId(), loginDTO.getUsername(), user.getRole());

            userService.updateLastLoginTime(user.getId());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            return Result.success(data);

        } catch (Exception e) {
            log.error("登录异常", e);
            return Result.error("用户名或密码错误");
        }
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        // 1. 检查用户名是否存在
        if (userService.existsByUsername(registerDTO.getUsername())) {
            return Result.error(1001, "用户名已存在");
        }

        // 2. 检查邮箱是否存在（如果提供）
        if (registerDTO.getEmail() != null && userService.existsByEmail(registerDTO.getEmail())) {
            return Result.error(1002, "邮箱已被注册");
        }

        // 3. 检查手机号是否存在（如果提供）
        if (registerDTO.getPhone() != null && userService.existsByPhone(registerDTO.getPhone())) {
            return Result.error(1003, "手机号已被注册");
        }

        // 4. 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        // 使用 DTO 中的角色，默认 JOB_SEEKER
        String role = registerDTO.getRole();
        if (role == null || (!role.equals("JOB_SEEKER") && !role.equals("HR") && !role.equals("ADMIN"))) {
            role = "JOB_SEEKER";
        }
        user.setRole(role);
        user.setStatus(1);

        // 5. 保存用户
        userService.save(user);

        return Result.success("注册成功");
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("退出成功");
    }
}
