package com.echo.blog.model.vo;

import lombok.Data;

/**
 * 热点VO
 */
@Data
public class HotspotVO {
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 热度
     */
    private String hot;
    
    /**
     * URL
     */
    private String url;
    
    /**
     * 来源
     */
    private String source;
    
    /**
     * 摘要
     */
    private String summary;
} 