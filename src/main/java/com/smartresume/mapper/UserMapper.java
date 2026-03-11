package com.smartresume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartresume.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(@Param("phone") String phone);

    @Update("UPDATE user SET last_login_time = NOW() WHERE id = #{userId}")
    void updateLastLoginTime(@Param("userId") Long userId);
}