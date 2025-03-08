package com.echo.blog.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 内容生成 DTO
 */
@Data
public class ContentGenerationDTO {
    
    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;
    
    /**
     * AI模型
     */
    private String aiModel;
} 