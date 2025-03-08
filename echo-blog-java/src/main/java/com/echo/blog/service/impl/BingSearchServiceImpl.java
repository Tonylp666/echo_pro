package com.echo.blog.service.impl;

import com.echo.blog.config.BingSearchProperties;
import com.echo.blog.model.vo.SearchResultVO;
import com.echo.blog.service.SearchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Bing搜索服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BingSearchServiceImpl implements SearchService {
    
    private final BingSearchProperties bingSearchProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Override
    public List<SearchResultVO> search(String keywords, int count) {
        List<SearchResultVO> results = new ArrayList<>();
        
        try {
            // 构建请求URL
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(bingSearchProperties.getEndpoint())
                    .queryParam("q", keywords)
                    .queryParam("count", count > 0 ? count : bingSearchProperties.getCount())
                    .queryParam("mkt", bingSearchProperties.getMarket());
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Ocp-Apim-Subscription-Key", bingSearchProperties.getApiKey());
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );
            
            // 解析响应
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode webPages = rootNode.path("webPages");
            JsonNode values = webPages.path("value");
            
            if (values.isArray()) {
                for (JsonNode value : values) {
                    SearchResultVO result = new SearchResultVO();
                    result.setTitle(value.path("name").asText());
                    result.setSnippet(value.path("snippet").asText());
                    result.setUrl(value.path("url").asText());
                    
                    // 尝试提取来源和日期
                    if (value.has("datePublished")) {
                        result.setPublishedDate(value.path("datePublished").asText());
                    }
                    
                    if (value.has("displayUrl")) {
                        String displayUrl = value.path("displayUrl").asText();
                        int endIndex = displayUrl.indexOf('/');
                        result.setSource(endIndex > 0 ? displayUrl.substring(0, endIndex) : displayUrl);
                    }
                    
                    results.add(result);
                }
            }
        } catch (Exception e) {
            log.error("Bing搜索API调用失败", e);
        }
        
        return results;
    }
} 