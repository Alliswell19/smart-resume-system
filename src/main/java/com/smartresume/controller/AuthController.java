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

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
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
    public Result login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            System.out.println("========== 登录尝试 ==========");
            System.out.println("用户名: " + loginDTO.getUsername());
            System.out.println("密码: " + loginDTO.getPassword());

            // 手动验证用户是否存在
            User user = userService.findByUsername(loginDTO.getUsername());
            if (user == null) {
                System.out.println("用户不存在");
                return Result.error("用户名或密码错误");
            }

            System.out.println("数据库中的用户: " + user);
            System.out.println("数据库中的密码哈希: " + user.getPassword());

            // 手动验证密码
            boolean passwordMatches = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
            System.out.println("密码手动验证结果: " + passwordMatches);

            if (!passwordMatches) {
                System.out.println("密码不匹配");
                return Result.error("用户名或密码错误");
            }

            // 使用 Spring Security 验证
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(loginDTO.getUsername());

            // 更新最后登录时间
            userService.updateLastLoginTime(user.getId());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            System.out.println("登录成功，token: " + token);
            System.out.println("=============================");

            return Result.success(data);

        } catch (Exception e) {
            System.out.println("登录异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("用户名或密码错误");
        }
    }

    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterDTO registerDTO) {
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
        user.setRole("USER");
        user.setStatus(1);

        // 5. 保存用户
        userService.save(user);

        return Result.success("注册成功");
    }

    @PostMapping("/logout")
    public Result logout() {
        SecurityContextHolder.clearContext();
        return Result.success("退出成功");
    }
}