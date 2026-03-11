package com.smartresume.service;

import com.smartresume.entity.User;
import com.smartresume.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("CustomUserDetailsService.loadUserByUsername: " + username);

        User user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("用户不存在: " + username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        System.out.println("找到用户: " + user.getUsername());
        System.out.println("用户密码: " + user.getPassword());
        System.out.println("用户角色: " + user.getRole());

        return user;
    }
}