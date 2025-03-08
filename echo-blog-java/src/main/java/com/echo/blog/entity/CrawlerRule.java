package com.echo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 爬虫规则实体类
 */
@Data
@Accessors(chain = true)
@TableName("a_crawler_rule")
public class CrawlerRule {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 规则名称
     */
    private String ruleName;
    
    /**
     * 目标网站URL
     */
    private String targetUrl;
    
    /**
     * 列表选择器
     */
    private String listSelector;
    
    /**
     * 标题选择器
     */
    private String titleSelector;
    
    /**
     * 内容选择器
     */
    private String contentSelector;
    
    /**
     * 日期选择器
     */
    private String dateSelector;
    
    /**
     * 作者选择器
     */
    private String authorSelector;
    
    /**
     * 请求头（JSON格式）
     */
    private String headers;
    
    /**
     * 状态：ENABLED(启用), DISABLED(禁用)
     */
    private String status;
    
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