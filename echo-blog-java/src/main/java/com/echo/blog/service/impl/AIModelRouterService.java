package com.echo.blog.service.impl;

import com.echo.blog.model.enums.ContentType;
import com.echo.blog.service.ai.BailianService;
import com.echo.blog.service.ai.DeepSeekService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI模型路由服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIModelRouterService {
    
    private final DeepSeekService deepSeekService;
    private final BailianService bailianService;
    
    /**
     * 生成内容
     *
     * @param sourceData 源数据
     * @param prompt 提示词
     * @param aiModel AI模型
     * @param contentType 内容类型
     * @return 生成的内容
     */
    public String generateContent(String sourceData, String prompt, String aiModel, ContentType contentType) {
        log.info("使用模型 {} 生成 {} 类型内容", aiModel, contentType);
        
        if ("deepseek".equalsIgnoreCase(aiModel)) {
            return deepSeekService.generateContent(sourceData, prompt);
        } else if ("bailian".equalsIgnoreCase(aiModel)) {
            return bailianService.generateContent(sourceData, prompt);
        } else {
            log.warn("未知的AI模型: {}, 使用默认模型", aiModel);
            return deepSeekService.generateContent(sourceData, prompt);
        }
    }

    /**
     * 转换为Markdown
     *
     * @param content 内容
     * @param contentType 内容类型
     * @return 转换后的Markdown
     */
    public String convertToMarkdown(String content, ContentType contentType) {
        return switch (contentType) {
            case WECHAT_ARTICLE -> deepSeekService.markdownConverter(content);
            case ZHIHU_ANSWER -> bailianService.formatForZhihu(content);
            default -> content;
        };
    }
}