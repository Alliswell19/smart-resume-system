package com.smartresume.service;

import com.smartresume.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    Optional<User> findByEmail(String email);

    // ✅ 删除 login() 方法！
    // String login(String username, String password); ← 删除这行
    User findByUsername(String username);
    boolean checkPassword(String rawPassword, String encodedPassword);
    User save(User user);
    User register(User user);
}