package com.echo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 文章任务实体类
 */
@Data
@Accessors(chain = true)
@TableName("a_article_task")
public class ArticleTask {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 文章分类ID
     */
    private Long articleCategoryId;
    
    /**
     * 提示词
     */
    private String prompt;
    
    /**
     * 源数据
     */
    private String sourceData;
    
    /**
     * 源类型
     */
    private String sourceType;
    
    /**
     * 结果文章ID
     */
    private Long resultArticleId;
    
    /**
     * 任务状态
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
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 调度类型
     */
    private String scheduleType;
    
    /**
     * 调度表达式
     */
    private String scheduleCron;
    
    /**
     * 上次执行时间
     */
    private LocalDateTime lastExecuteTime;
    
    /**
     * 下次执行时间
     */
    private LocalDateTime nextExecuteTime;
}