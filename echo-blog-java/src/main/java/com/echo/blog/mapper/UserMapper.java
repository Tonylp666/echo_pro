package com.echo.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echo.blog.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    User selectByUsername(String name);

    User selectByEmail(String email);
}