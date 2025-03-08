package com.echo.blog.model.vo;

import com.echo.blog.enums.PlatformType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户平台授权VO
 */
@Data
public class UserPlatformAuthVO {
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 平台类型
     */
    private PlatformType platformType;
    
    /**
     * 平台用户ID
     */
    private String platformUserId;
    
    /**
     * 平台用户名
     */
    private String platformUserName;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
} 