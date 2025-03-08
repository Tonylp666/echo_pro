package com.echo.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 爬虫规则 VO
 */
@Data
@Schema(description = "爬虫规则VO")
public class CrawlerRuleVO {
    
    /**
     * ID
     */
    @Schema(description = "规则ID")
    private Long id;
    
    /**
     * 规则名称
     */
    @Schema(description = "规则名称")
    private String ruleName;
    
    /**
     * 目标网站URL
     */
    @Schema(description = "目标网站URL")
    private String targetUrl;
    
    /**
     * 列表选择器
     */
    @Schema(description = "列表选择器")
    private String listSelector;
    
    /**
     * 标题选择器
     */
    @Schema(description = "标题选择器")
    private String titleSelector;
    
    /**
     * 内容选择器
     */
    @Schema(description = "内容选择器")
    private String contentSelector;
    
    /**
     * 日期选择器
     */
    @Schema(description = "日期选择器")
    private String dateSelector;
    
    /**
     * 作者选择器
     */
    @Schema(description = "作者选择器")
    private String authorSelector;
    
    /**
     * 请求头（JSON格式）
     */
    @Schema(description = "请求头（JSON格式）")
    private String headers;
    
    /**
     * 状态：ENABLED(启用), DISABLED(禁用)
     */
    @Schema(description = "状态：ENABLED(启用), DISABLED(禁用)")
    private String status;
    
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createdBy;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
} 