package com.smartresume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartresume.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    User findByEmail(String email);
}