package com.smartresume.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartresume.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService extends IService<User> {

    /**
     * 保存用户并返回完整实体（含自增ID等）
     * @param user 用户实体
     * @return 保存后的用户
     */
    User saveAndReturn(User user);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户
     */
    User findByUsername(String username);

    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户Optional
     */
    Optional<User> findByPhone(String phone);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 更新最后登录时间
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);

    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean checkPassword(String rawPassword, String encodedPassword);

}