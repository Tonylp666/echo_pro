package com.echo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * AI内容生成任务实体类
 */
@Data
@Accessors(chain = true)
@TableName("a_ai_generation_task")
public class AIGenerationTask {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务类型：CRAWLER(爬虫), SEARCH(搜索), HOTSPOT(热点)
     */
    private String taskType;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 源数据（JSON格式）
     */
    private String sourceData;
    
    /**
     * 提示词
     */
    private String prompt;
    
    /**
     * 使用的AI模型
     */
    private String aiModel;
    
    /**
     * 生成的文章ID
     */
    private Long resultArticleId;
    
    /**
     * 任务状态：PENDING(待处理), PROCESSING(处理中), COMPLETED(已完成), FAILED(失败)
     */
    private String status;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建者
     */
    private String createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
} 