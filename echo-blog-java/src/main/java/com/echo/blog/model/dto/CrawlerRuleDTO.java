package com.echo.blog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 爬虫规则DTO
 */
@Data
@Schema(description = "爬虫规则DTO")
public class CrawlerRuleDTO {
    
    @Schema(description = "规则名称")
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;
    
    @Schema(description = "目标网站URL")
    @NotBlank(message = "目标网站URL不能为空")
    private String targetUrl;
    
    @Schema(description = "列表选择器")
    @NotBlank(message = "列表选择器不能为空")
    private String listSelector;
    
    @Schema(description = "标题选择器")
    @NotBlank(message = "标题选择器不能为空")
    private String titleSelector;
    
    @Schema(description = "内容选择器")
    @NotBlank(message = "内容选择器不能为空")
    private String contentSelector;
    
    @Schema(description = "日期选择器")
    private String dateSelector;
    
    @Schema(description = "作者选择器")
    private String authorSelector;
    
    @Schema(description = "请求头（JSON格式）")
    private String headers;
    
    @Schema(description = "状态：ENABLED(启用), DISABLED(禁用)")
    private String status = "ENABLED";
} 