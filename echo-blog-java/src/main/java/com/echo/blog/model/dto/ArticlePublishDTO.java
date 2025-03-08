package com.echo.blog.model.dto;

import com.echo.blog.enums.PlatformType;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * 文章发布 DTO
 */
@Data
public class ArticlePublishDTO {
    
    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;
    
    /**
     * 平台类型
     */
    @NotNull(message = "平台类型不能为空")
    private PlatformType platformType;
    
    /**
     * 平台配置
     */
    private Map<String, Object> config;
} 