package com.echo.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 平台配置VO
 */
@Data
@Schema(description = "平台配置VO")
public class PlatformConfigVO {
    
    @Schema(description = "配置ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "平台类型：WECHAT(微信公众号), ZHIHU(知乎), TOUTIAO(今日头条), XIAOHONGSHU(小红书)")
    private String platformType;
    
    @Schema(description = "平台名称")
    private String platformName;
    
    @Schema(description = "平台图标")
    private String platformIcon;
    
    @Schema(description = "授权凭证（已保护）")
    private String authCredential;
    
    @Schema(description = "授权过期时间")
    private LocalDateTime expireTime;
    
    @Schema(description = "刷新令牌（已保护）")
    private String refreshToken;
    
    @Schema(description = "额外配置（JSON格式）")
    private String extraConfig;
    
    @Schema(description = "状态：ENABLED(启用), DISABLED(禁用)")
    private String status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
} 