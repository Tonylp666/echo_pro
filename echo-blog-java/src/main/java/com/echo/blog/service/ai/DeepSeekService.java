package com.echo.blog.service.ai;

import com.echo.blog.config.AIModelProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek AI 服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekService {
    
    private final AIModelProperties aiModelProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    /**
     * 生成内容
     *
     * @param sourceData 源数据
     * @param prompt 提示词
     * @return 生成的内容
     */
    public String generateContent(String sourceData, String prompt) {
        try {
            AIModelProperties.DeepSeek config = aiModelProperties.getDeepseek();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + config.getApiKey());
            
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", "源数据：" + sourceData + "\n\n提示词：" + prompt);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModel());
            requestBody.put("messages", List.of(message));
            requestBody.put("max_tokens", config.getMaxTokens());
            requestBody.put("temperature", config.getTemperature());
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            Map<String, Object> response = restTemplate.postForObject(
                    config.getApiUrl(),
                    request,
                    Map.class
            );
            
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> responseMessage = (Map<String, Object>) choice.get("message");
                    return (String) responseMessage.get("content");
                }
            }
            
            log.error("DeepSeek API 响应格式错误: {}", response);
            return "内容生成失败，请稍后重试";
        } catch (Exception e) {
            log.error("DeepSeek API 调用失败", e);
            return "内容生成失败，请稍后重试";
        }
    }
    
    /**
     * 将内容转换为微信文章格式的Markdown
     *
     * @param content 原始内容
     * @return 转换后的Markdown
     */
    public String markdownConverter(String content) {
        try {
            AIModelProperties.DeepSeek config = aiModelProperties.getDeepseek();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + config.getApiKey());
            
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", "请将以下内容转换为适合微信公众号的Markdown格式：\n\n" + content);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModel());
            requestBody.put("messages", List.of(message));
            requestBody.put("max_tokens", config.getMaxTokens());
            requestBody.put("temperature", 0.2);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            Map<String, Object> response = restTemplate.postForObject(
                    config.getApiUrl(),
                    request,
                    Map.class
            );
            
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> responseMessage = (Map<String, Object>) choice.get("message");
                    return (String) responseMessage.get("content");
                }
            }
            
            log.error("DeepSeek API 响应格式错误: {}", response);
            return content;
        } catch (Exception e) {
            log.error("DeepSeek API 调用失败", e);
            return content;
        }
    }
} 