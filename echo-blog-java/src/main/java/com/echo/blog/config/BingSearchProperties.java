package com.echo.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bing Search API 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "bing.search")
public class BingSearchProperties {
    
    /**
     * API 密钥
     */
    private String apiKey;
    
    /**
     * API 端点
     */
    private String endpoint = "https://api.bing.microsoft.com/v7.0/search";
    
    /**
     * 默认市场
     */
    private String market = "zh-CN";
    
    /**
     * 默认结果数量
     */
    private int count = 10;
} 