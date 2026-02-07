package com.smartresume.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    // 假设您有一个 UserService 或直接从 JWT 中提取用户信息
    // 这里我们先用一个简单的示例

    @GetMapping("/info")
    public String getUserInfo() {
        // 在实际项目中，这里应该从 JWT Token 中解析出用户 ID，
        // 然后查询数据库获取用户详细信息。
        // 为了演示，我们返回一个固定字符串。
        return "Hello, User! This is your info.";
    }
}