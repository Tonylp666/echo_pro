package com.echo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("a_permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String code;
    
    private String type;
    
    private Long parentId;
    
    private String path;
    
    private String component;
    
    private String icon;
    
    private Integer sort;
    
    private String status;
    
    private LocalDateTime createdTime;
    
    private LocalDateTime updatedTime;
}