package com.smartresume.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartresume.entity.User;
import com.smartresume.mapper.UserMapper;
import com.smartresume.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== UserService 自定义方法 ====================

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userMapper.findByEmail(email));
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return Optional.ofNullable(this.getOne(wrapper));
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.findByUsername(username) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.findByEmail(email) != null;
    }

    @Override
    public boolean existsByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return this.count(wrapper) > 0;
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId)
               .set(User::getLastLoginTime, LocalDateTime.now());
        this.update(wrapper);
    }

    @Override
    public User saveAndReturn(User user) {
        // 保存前对密码进行加密
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userMapper.insert(user);
        return user;
    }

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            return false;
        }
        
        // 验证旧密码
        if (!checkPassword(oldPassword, user.getPassword())) {
            return false;
        }
        
        // 更新新密码
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId)
               .set(User::getPassword, passwordEncoder.encode(newPassword))
               .set(User::getUpdateTime, LocalDateTime.now());
        return this.update(wrapper);
    }

    // ==================== 管理员功能方法 ====================

    @Override
    public java.util.List<User> getUserList(int page, int pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> pageInfo = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);
        
        return this.page(pageInfo, wrapper).getRecords();
    }

    @Override
    public long getUserCount() {
        return this.count();
    }

    @Override
    public long getActiveUserCount() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, 1);
        return this.count(wrapper);
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId)
               .set(User::getStatus, status)
               .set(User::getUpdateTime, LocalDateTime.now());
        return this.update(wrapper);
    }
}