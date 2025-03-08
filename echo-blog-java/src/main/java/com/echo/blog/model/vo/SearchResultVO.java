package com.echo.blog.model.vo;

import lombok.Data;

/**
 * 搜索结果VO
 */
@Data
public class SearchResultVO {
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 摘要
     */
    private String snippet;
    
    /**
     * URL
     */
    private String url;
    
    /**
     * 来源
     */
    private String source;
    
    /**
     * 发布日期
     */
    private String publishedDate;
} 