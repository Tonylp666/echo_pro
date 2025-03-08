package com.echo.blog.service.impl;

import com.echo.blog.entity.Article;
import com.echo.blog.entity.ArticleTask;
import com.echo.blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIArticleGenerator {

    private final ArticleService articleService;
    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    public Article generateArticle(ArticleTask task) {
        String prompt = task.getPrompt();
        String content = generateContentFromOpenAI(prompt);

        Article article = new Article()
                .setTitle(generateTitle(content))
                .setContent(content)
                .setArticleCategoryId(task.getArticleCategoryId())
                .setStatus("DRAFT")
                .setCreatedBy(task.getCreatedBy())
                .setCreatedTime(LocalDateTime.now())
                .setUpdatedTime(LocalDateTime.now())
                .setViewCount(0L)
                .setLikeCount(0L);

        return articleService.createArticle(article);
    }

    private String generateContentFromOpenAI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new Object[]{
            new HashMap<String, String>() {{
                put("role", "system");
                put("content", "你是一个专业的文章创作者，请根据提示创作高质量的文章。");
            }},
            new HashMap<String, String>() {{
                put("role", "user");
                put("content", prompt);
            }}
        });
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(
                openaiApiUrl + "/chat/completions",
                request,
                Map.class
            );

            if (response != null && response.containsKey("choices")) {
                Object[] choices = (Object[]) response.get("choices");
                if (choices.length > 0) {
                    Map<String, Object> choice = (Map<String, Object>) choices[0];
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    return message.get("content");
                }
            }
            throw new RuntimeException("Failed to generate content from OpenAI");
        } catch (Exception e) {
            throw new RuntimeException("Error calling OpenAI API: " + e.getMessage(), e);
        }
    }

    private String generateTitle(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new Object[]{
            new HashMap<String, String>() {{
                put("role", "system");
                put("content", "请为以下文章内容生成一个吸引人的标题，不超过50个字。");
            }},
            new HashMap<String, String>() {{
                put("role", "user");
                put("content", content);
            }}
        });
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 100);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(
                openaiApiUrl + "/chat/completions",
                request,
                Map.class
            );

            if (response != null && response.containsKey("choices")) {
                Object[] choices = (Object[]) response.get("choices");
                if (choices.length > 0) {
                    Map<String, Object> choice = (Map<String, Object>) choices[0];
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    return message.get("content").trim();
                }
            }
            throw new RuntimeException("Failed to generate title from OpenAI");
        } catch (Exception e) {
            throw new RuntimeException("Error calling OpenAI API: " + e.getMessage(), e);
        }
    }
}