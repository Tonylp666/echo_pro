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
import java.util.Map;

/**
 * 阿里云百炼 AI 服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BailianService {
    
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
            AIModelProperties.Aliyun config = aiModelProperties.getAliyun();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + config.getAccessKeyId() + ":" + config.getAccessKeySecret());
            
            Map<String, Object> input = new HashMap<>();
            input.put("prompt", "源数据：" + sourceData + "\n\n提示词：" + prompt);
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("result_format", "text");
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModel());
            requestBody.put("input", input);
            requestBody.put("parameters", parameters);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            String url = "https://" + config.getEndpoint() + "/api/v1/services/aigc/text-generation/generation";
            
            Map<String, Object> response = restTemplate.postForObject(
                    url,
                    request,
                    Map.class
            );
            
            if (response != null && response.containsKey("output")) {
                Map<String, Object> output = (Map<String, Object>) response.get("output");
                if (output.containsKey("text")) {
                    return (String) output.get("text");
                }
            }
            
            log.error("阿里云百炼 API 响应格式错误: {}", response);
            return "内容生成失败，请稍后重试";
        } catch (Exception e) {
            log.error("阿里云百炼 API 调用失败", e);
            return "内容生成失败，请稍后重试";
        }
    }
    
    /**
     * 将内容格式化为知乎回答格式
     *
     * @param content 原始内容
     * @return 格式化后的内容
     */
    public String formatForZhihu(String content) {
        try {
            AIModelProperties.Aliyun config = aiModelProperties.getAliyun();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + config.getAccessKeyId() + ":" + config.getAccessKeySecret());
            
            Map<String, Object> input = new HashMap<>();
            input.put("prompt", "请将以下内容格式化为适合知乎回答的格式，保持内容的专业性和可读性：\n\n" + content);
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("result_format", "text");
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModel());
            requestBody.put("input", input);
            requestBody.put("parameters", parameters);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            String url = "https://" + config.getEndpoint() + "/api/v1/services/aigc/text-generation/generation";
            
            Map<String, Object> response = restTemplate.postForObject(
                    url,
                    request,
                    Map.class
            );
            
            if (response != null && response.containsKey("output")) {
                Map<String, Object> output = (Map<String, Object>) response.get("output");
                if (output.containsKey("text")) {
                    return (String) output.get("text");
                }
            }
            
            log.error("阿里云百炼 API 响应格式错误: {}", response);
            return content;
        } catch (Exception e) {
            log.error("阿里云百炼 API 调用失败", e);
            return content;
        }
    }
} 