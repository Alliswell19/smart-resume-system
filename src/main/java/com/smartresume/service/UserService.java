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

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取用户列表（分页查询）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    java.util.List<User> getUserList(int page, int pageSize);

    /**
     * 获取用户总数
     * @return 用户总数
     */
    long getUserCount();

    /**
     * 获取活跃用户数
     * @return 活跃用户数
     */
    long getActiveUserCount();

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态（1-启用，0-禁用）
     * @return 是否更新成功
     */
    boolean updateUserStatus(Long userId, Integer status);

}