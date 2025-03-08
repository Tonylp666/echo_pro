package com.echo.blog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * AI 生成任务 DTO
 */
@Data
@Schema(description = "AI内容生成任务DTO")
public class AIGenerationTaskDTO {
    
    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    private String taskName;
    
    /**
     * 任务类型
     */
    @Schema(description = "任务类型：CRAWLER(爬虫), SEARCH(搜索), HOTSPOT(热点)")
    @NotBlank(message = "任务类型不能为空")
    private String taskType;
    
    /**
     * 爬虫规则ID
     */
    @Schema(description = "爬虫规则ID")
    private Long crawlerRuleId;
    
    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词")
    private String searchKeywords;
    
    /**
     * 热点来源
     */
    @Schema(description = "热点来源")
    private String hotspotSource;
    
    /**
     * AI模型
     */
    @Schema(description = "使用的AI模型")
    @NotBlank(message = "AI模型不能为空")
    private String aiModel;
    
    /**
     * 提示词
     */
    @Schema(description = "提示词")
    private String prompt;
    
    /**
     * 文章分类ID
     */
    @Schema(description = "文章分类ID")
    @NotNull(message = "文章分类不能为空")
    private Long articleCategoryId;
    
    /**
     * 源数据（JSON格式）
     */
    @Schema(description = "源数据（JSON格式）")
    private String sourceData;
} 