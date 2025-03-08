package com.echo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("a_role_permission")
public class RolePermission {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long roleId;
    
    private Long permissionId;
    
    private LocalDateTime createdTime;
}