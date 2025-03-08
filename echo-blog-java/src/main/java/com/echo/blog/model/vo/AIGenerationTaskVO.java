package com.echo.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 生成任务 VO
 */
@Data
@Schema(description = "AI内容生成任务VO")
public class AIGenerationTaskVO {
    
    /**
     * ID
     */
    @Schema(description = "任务ID")
    private Long id;
    
    /**
     * 任务类型
     */
    @Schema(description = "任务类型：CRAWLER(爬虫), SEARCH(搜索), HOTSPOT(热点)")
    private String taskType;
    
    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String taskName;
    
    /**
     * 任务描述
     */
    @Schema(description = "任务描述")
    private String description;
    
    /**
     * 源数据（JSON格式）
     */
    @Schema(description = "源数据（JSON格式）")
    private String sourceData;
    
    /**
     * 提示词
     */
    @Schema(description = "提示词")
    private String prompt;
    
    /**
     * 使用的AI模型
     */
    @Schema(description = "使用的AI模型")
    private String aiModel;
    
    /**
     * 生成的文章ID
     */
    @Schema(description = "生成的文章ID")
    private Long resultArticleId;
    
    /**
     * 任务状态
     */
    @Schema(description = "任务状态：PENDING(待处理), PROCESSING(处理中), COMPLETED(已完成), FAILED(失败)")
    private String status;
    
    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMessage;
    
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