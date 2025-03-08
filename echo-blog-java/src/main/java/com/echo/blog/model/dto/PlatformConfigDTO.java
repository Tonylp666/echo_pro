package com.echo.blog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 平台配置DTO
 */
@Data
@Schema(description = "平台配置DTO")
public class PlatformConfigDTO {
    
    @Schema(description = "平台类型：WECHAT(微信公众号), ZHIHU(知乎), TOUTIAO(今日头条), XIAOHONGSHU(小红书)")
    @NotBlank(message = "平台类型不能为空")
    private String platformType;
    
    @Schema(description = "平台名称")
    @NotBlank(message = "平台名称不能为空")
    private String platformName;
    
    @Schema(description = "平台图标")
    private String platformIcon;
    
    @Schema(description = "授权凭证")
    @NotBlank(message = "授权凭证不能为空")
    private String authCredential;
    
    @Schema(description = "授权过期时间")
    private LocalDateTime expireTime;
    
    @Schema(description = "刷新令牌")
    private String refreshToken;
    
    @Schema(description = "额外配置（JSON格式）")
    private String extraConfig;
    
    @Schema(description = "状态：ENABLED(启用), DISABLED(禁用)")
    private String status = "ENABLED";
} 