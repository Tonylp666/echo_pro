package com.echo.blog.model.vo;

import com.echo.blog.enums.PlatformType;
import lombok.Data;

/**
 * 文章发布结果 VO
 */
@Data
public class ArticlePublishResultVO {
    
    /**
     * 文章ID
     */
    private Long articleId;
    
    /**
     * 平台类型
     */
    private PlatformType platformType;
    
    /**
     * 平台文章ID
     */
    private String platformArticleId;
    
    /**
     * 平台文章URL
     */
    private String platformArticleUrl;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 错误信息
     */
    private String errorMessage;
} 