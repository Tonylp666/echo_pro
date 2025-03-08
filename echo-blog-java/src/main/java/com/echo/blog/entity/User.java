package com.echo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@TableName("a_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private String password;

    private String portrait;

    private String status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
    
    // 非数据库字段，用于存储用户角色
    private transient List<String> roles;
}