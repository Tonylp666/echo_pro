package com.echo.blog.service.impl;

import com.echo.blog.model.vo.HotspotVO;
import com.echo.blog.service.HotspotService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 热点服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HotspotServiceImpl implements HotspotService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String BAIDU_HOT_API = "https://api.oioweb.cn/api/common/HotList?type=baidu";
    private static final String WEIBO_HOT_API = "https://api.oioweb.cn/api/common/HotList?type=weibo";
    private static final String ZHIHU_HOT_API = "https://api.oioweb.cn/api/common/HotList?type=zhihu";
    private static final String TOUTIAO_HOT_API = "https://api.oioweb.cn/api/common/HotList?type=toutiao";
    
    @Override
    public List<HotspotVO> getHotspots(String source, int count) {
        String apiUrl;
        
        // 根据来源选择API
        switch (source.toUpperCase()) {
            case "BAIDU":
                apiUrl = BAIDU_HOT_API;
                break;
            case "WEIBO":
                apiUrl = WEIBO_HOT_API;
                break;
            case "ZHIHU":
                apiUrl = ZHIHU_HOT_API;
                break;
            case "TOUTIAO":
                apiUrl = TOUTIAO_HOT_API;
                break;
            default:
                apiUrl = BAIDU_HOT_API;
        }
        
        return fetchHotspots(apiUrl, source, count);
    }
    
    /**
     * 获取热点数据
     *
     * @param apiUrl API地址
     * @param source 来源
     * @param count 数量
     * @return 热点列表
     */
    private List<HotspotVO> fetchHotspots(String apiUrl, String source, int count) {
        List<HotspotVO> results = new ArrayList<>();
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            
            // 检查API响应状态
            if (rootNode.has("code") && rootNode.get("code").asInt() == 200) {
                JsonNode data = rootNode.path("data");
                
                if (data.isArray()) {
                    int counter = 0;
                    for (JsonNode item : data) {
                        if (counter >= count) {
                            break;
                        }
                        
                        HotspotVO hotspot = new HotspotVO();
                        hotspot.setTitle(item.path("title").asText());
                        hotspot.setUrl(item.path("url").asText());
                        hotspot.setHot(item.path("hot").asText());
                        hotspot.setSource(source.toUpperCase());
                        
                        if (item.has("desc")) {
                            hotspot.setSummary(item.path("desc").asText());
                        }
                        
                        results.add(hotspot);
                        counter++;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取热点数据失败: {}", source, e);
        }
        
        return results;
    }
} 