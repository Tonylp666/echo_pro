package com.echo.blog.model.dto;

import com.echo.blog.enums.PlatformType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 平台授权DTO
 */
@Data
public class PlatformAuthDTO {
    
    /**
     * 平台类型
     */
    @NotNull(message = "平台类型不能为空")
    private PlatformType platformType;
    
    /**
     * 授权令牌
     */
    @NotNull(message = "授权令牌不能为空")
    private String authToken;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 平台用户ID
     */
    private String platformUserId;
    
    /**
     * 平台用户名
     */
    private String platformUserName;
} 