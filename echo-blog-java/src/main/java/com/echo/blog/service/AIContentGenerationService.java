package com.echo.blog.service;

import com.echo.blog.entity.AIGenerationTask;
import com.echo.blog.entity.Article;

/**
 * AI内容生成服务接口
 */
public interface AIContentGenerationService {
    
    /**
     * 根据爬虫数据生成文章
     * @param task 生成任务
     * @return 生成的文章
     */
    Article generateFromCrawler(AIGenerationTask task);
    
    /**
     * 根据搜索数据生成文章
     * @param task 生成任务
     * @return 生成的文章
     */
    Article generateFromSearch(AIGenerationTask task);
    
    /**
     * 根据热点数据生成文章
     * @param task 生成任务
     * @return 生成的文章
     */
    Article generateFromHotspot(AIGenerationTask task);
    
    /**
     * 处理AI生成任务
     * @param taskId 任务ID
     * @return 处理结果
     */
    boolean processTask(Long taskId);
    
    /**
     * 生成标题
     * @param content 内容
     * @return 生成的标题
     */
    String generateTitle(String content);
    
    /**
     * 生成摘要
     * @param content 内容
     * @return 生成的摘要
     */
    String generateSummary(String content);
    
    /**
     * 优化内容
     * @param content 内容
     * @return 优化后的内容
     */
    String improveContent(String content);
} 