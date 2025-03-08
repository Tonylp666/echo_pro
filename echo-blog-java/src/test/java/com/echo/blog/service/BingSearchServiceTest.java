package com.echo.blog.service;

import com.echo.blog.config.BingSearchProperties;
import com.echo.blog.model.vo.SearchResultVO;
import com.echo.blog.service.impl.BingSearchServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BingSearchServiceTest {
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @Mock
    private BingSearchProperties bingSearchProperties;
    
    @InjectMocks
    private BingSearchServiceImpl searchService;
    
    @BeforeEach
    public void setup() {
        when(bingSearchProperties.getApiKey()).thenReturn("test-api-key");
        when(bingSearchProperties.getEndpoint()).thenReturn("https://api.bing.microsoft.com/v7.0/search");
        when(bingSearchProperties.getMarket()).thenReturn("zh-CN");
        when(bingSearchProperties.getCount()).thenReturn(10);
    }
    
    @Test
    public void testSearch() throws Exception {
        // 准备测试数据
        String mockResponse = "{"
                + "\"webPages\": {"
                + "  \"value\": ["
                + "    {"
                + "      \"name\": \"Test Title 1\","
                + "      \"url\": \"https://example.com/1\","
                + "      \"snippet\": \"Test snippet 1\","
                + "      \"datePublished\": \"2023-01-01\","
                + "      \"displayUrl\": \"example.com/1\""
                + "    },"
                + "    {"
                + "      \"name\": \"Test Title 2\","
                + "      \"url\": \"https://example.com/2\","
                + "      \"snippet\": \"Test snippet 2\","
                + "      \"datePublished\": \"2023-01-02\","
                + "      \"displayUrl\": \"example.com/2\""
                + "    }"
                + "  ]"
                + "}"
                + "}";
        
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);
        
        when(objectMapper.readTree(anyString())).thenCallRealMethod();
        
        // 执行测试
        List<SearchResultVO> results = searchService.search("test", 2);
        
        // 验证结果
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Test Title 1", results.get(0).getTitle());
        assertEquals("https://example.com/1", results.get(0).getUrl());
        assertEquals("Test snippet 1", results.get(0).getSnippet());
        assertEquals("2023-01-01", results.get(0).getPublishedDate());
        assertEquals("example.com", results.get(0).getSource());
    }
} 