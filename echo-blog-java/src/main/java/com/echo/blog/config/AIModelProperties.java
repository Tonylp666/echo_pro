package com.echo.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI模型配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai.model")
public class AIModelProperties {
    
    /**
     * DeepSeek API配置
     */
    private DeepSeek deepseek = new DeepSeek();
    
    /**
     * 阿里云百炼配置
     */
    private Aliyun aliyun = new Aliyun();
    
    /**
     * Bing Search API配置
     */
    private BingSearch bingSearch = new BingSearch();
    
    @Data
    public static class DeepSeek {
        /**
         * API密钥
         */
        private String apiKey;
        
        /**
         * API URL
         */
        private String apiUrl = "https://api.deepseek.com/v1/chat/completions";
        
        /**
         * 模型名称
         */
        private String model = "deepseek-chat";
        
        /**
         * 最大令牌数
         */
        private Integer maxTokens = 2000;
        
        /**
         * 温度
         */
        private Double temperature = 0.7;
    }
    
    @Data
    public static class Aliyun {
        /**
         * 访问密钥ID
         */
        private String accessKeyId;
        
        /**
         * 访问密钥密码
         */
        private String accessKeySecret;
        
        /**
         * 端点
         */
        private String endpoint = "dashscope.aliyuncs.com";
        
        /**
         * 模型名称
         */
        private String model = "qwen-max";
    }
    
    @Data
    public static class BingSearch {
        /**
         * API密钥
         */
        private String apiKey;
        
        /**
         * API URL
         */
        private String apiUrl = "https://api.bing.microsoft.com/v7.0/search";
        
        /**
         * 每页结果数
         */
        private Integer count = 10;
    }
}