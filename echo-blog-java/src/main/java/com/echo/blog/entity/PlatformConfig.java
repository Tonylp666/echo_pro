package com.echo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.echo.blog.enums.PlatformType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 平台配置实体类
 */
@Data
@Accessors(chain = true)
@TableName("a_platform_config")
public class PlatformConfig {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 平台类型
     */
    private String platformType;
    
    /**
     * 平台名称
     */
    private String platformName;
    
    /**
     * 平台图标
     */
    private String platformIcon;
    
    /**
     * 授权凭证
     */
    private String authCredential;
    
    /**
     * 授权过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 配置信息（JSON格式）
     */
    private String config;
    
    /**
     * 额外配置
     */
    private String extraConfig;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
} 