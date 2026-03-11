package com.smartresume.controller;

import com.smartresume.common.Result;
import com.smartresume.entity.User;
import com.smartresume.service.UserService;
import com.smartresume.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/info")
    public Result getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return Result.error(401, "未登录或token无效");
            }

            String jwtToken = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwtToken);

            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }

            return Result.success(user);
        } catch (Exception e) {
            return Result.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }
}