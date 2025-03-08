package com.echo.blog.model.vo;

import lombok.Data;

/**
 * 爬虫数据 VO
 */
@Data
public class CrawlerDataVO {
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * URL
     */
    private String url;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 日期
     */
    private String date;
    
    /**
     * 作者
     */
    private String author;
} 