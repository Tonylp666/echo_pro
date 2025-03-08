package com.echo.blog.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echo.blog.common.exception.BusinessException;
import com.echo.blog.entity.CrawlerRule;
import com.echo.blog.model.dto.CrawlerRuleDTO;
import com.echo.blog.model.vo.CrawlerDataVO;
import com.echo.blog.model.vo.CrawlerRuleVO;
import com.echo.blog.service.CrawlerRuleService;
import com.echo.blog.service.CrawlerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 爬虫服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {

    private final ObjectMapper objectMapper;
    private final CrawlerRuleService crawlerRuleService;

    @Override
    public List<CrawlerRuleVO> getRulesByUser(String username) {
        Page<CrawlerRule> page = new Page<>(1, 100);
        Page<CrawlerRule> rulePage = crawlerRuleService.getRulesByUser(username, page);
        return rulePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CrawlerRuleVO createRule(CrawlerRuleDTO ruleDTO, String username) {
        CrawlerRule rule = new CrawlerRule();
        BeanUtils.copyProperties(ruleDTO, rule);
        rule.setCreatedBy(username);
        
        CrawlerRule savedRule = crawlerRuleService.createRule(rule);
        return convertToVO(savedRule);
    }

    @Override
    public CrawlerRuleVO getRuleById(Long ruleId) {
        CrawlerRule rule = crawlerRuleService.getRuleById(ruleId);
        if (rule == null) {
            throw new BusinessException("爬虫规则不存在");
        }
        return convertToVO(rule);
    }

    @Override
    @Transactional
    public CrawlerRuleVO updateRule(Long ruleId, CrawlerRuleDTO ruleDTO) {
        CrawlerRule rule = crawlerRuleService.getRuleById(ruleId);
        if (rule == null) {
            throw new BusinessException("爬虫规则不存在");
        }
        
        BeanUtils.copyProperties(ruleDTO, rule);
        CrawlerRule updatedRule = crawlerRuleService.updateRule(rule);
        return convertToVO(updatedRule);
    }

    @Override
    @Transactional
    public CrawlerRuleVO updateRuleStatus(Long ruleId, String status) {
        CrawlerRule updatedRule = crawlerRuleService.updateRuleStatus(ruleId, status);
        if (updatedRule == null) {
            throw new BusinessException("爬虫规则不存在");
        }
        return convertToVO(updatedRule);
    }

    @Override
    @Transactional
    public boolean deleteRule(Long ruleId) {
        return crawlerRuleService.deleteRule(ruleId);
    }

    @Override
    public List<CrawlerDataVO> testRule(Long ruleId) {
        CrawlerRule rule = crawlerRuleService.getRuleById(ruleId);
        if (rule == null) {
            throw new BusinessException("爬虫规则不存在");
        }
        
        String jsonData = crawlContent(rule);
        return parseDataFromJson(jsonData);
    }

    @Override
    public List<CrawlerDataVO> getData(Long ruleId) {
        CrawlerRule rule = crawlerRuleService.getRuleById(ruleId);
        if (rule == null) {
            throw new BusinessException("爬虫规则不存在");
        }
        
        // 在实际应用中，这里应该从数据库中获取已爬取的数据
        // 这里为了简化，直接爬取新数据
        String jsonData = crawlContent(rule);
        return parseDataFromJson(jsonData);
    }
    
    /**
     * 将实体转换为VO
     */
    private CrawlerRuleVO convertToVO(CrawlerRule rule) {
        CrawlerRuleVO vo = new CrawlerRuleVO();
        BeanUtils.copyProperties(rule, vo);
        return vo;
    }
    
    /**
     * 解析JSON数据为VO列表
     */
    private List<CrawlerDataVO> parseDataFromJson(String jsonData) {
        try {
            List<Map<String, String>> dataList = objectMapper.readValue(jsonData, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            
            return dataList.stream().map(item -> {
                CrawlerDataVO vo = new CrawlerDataVO();
                vo.setTitle(item.get("title"));
                vo.setContent(item.get("content"));
                vo.setDate(item.get("date"));
                vo.setAuthor(item.get("author"));
                return vo;
            }).collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            log.error("解析爬虫数据失败：{}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 爬取内容
     */
    public String crawlContent(CrawlerRule rule) {
        try {
            log.info("开始爬取内容，规则：{}", rule.getRuleName());
            
            // 解析请求头
            Map<String, String> headers = parseHeaders(rule.getHeaders());
            
            // 爬取页面
            Document document = Jsoup.connect(rule.getTargetUrl())
                    .headers(headers)
                    .timeout(10000)
                    .get();
            
            // 提取内容
            List<Map<String, String>> results = new ArrayList<>();
            
            // 如果有列表选择器，则提取列表
            if (rule.getListSelector() != null && !rule.getListSelector().isEmpty()) {
                Elements elements = document.select(rule.getListSelector());
                for (Element element : elements) {
                    Map<String, String> item = new HashMap<>();
                    
                    // 提取标题
                    if (rule.getTitleSelector() != null && !rule.getTitleSelector().isEmpty()) {
                        Element titleElement = element.select(rule.getTitleSelector()).first();
                        if (titleElement != null) {
                            item.put("title", titleElement.text());
                        }
                    }
                    
                    // 提取内容
                    if (rule.getContentSelector() != null && !rule.getContentSelector().isEmpty()) {
                        Element contentElement = element.select(rule.getContentSelector()).first();
                        if (contentElement != null) {
                            item.put("content", contentElement.text());
                        }
                    }
                    
                    // 提取日期
                    if (rule.getDateSelector() != null && !rule.getDateSelector().isEmpty()) {
                        Element dateElement = element.select(rule.getDateSelector()).first();
                        if (dateElement != null) {
                            item.put("date", dateElement.text());
                        }
                    }
                    
                    // 提取作者
                    if (rule.getAuthorSelector() != null && !rule.getAuthorSelector().isEmpty()) {
                        Element authorElement = element.select(rule.getAuthorSelector()).first();
                        if (authorElement != null) {
                            item.put("author", authorElement.text());
                        }
                    }
                    
                    if (!item.isEmpty()) {
                        results.add(item);
                    }
                }
            } else {
                // 如果没有列表选择器，则直接提取内容
                Map<String, String> item = new HashMap<>();
                
                // 提取标题
                if (rule.getTitleSelector() != null && !rule.getTitleSelector().isEmpty()) {
                    Element titleElement = document.select(rule.getTitleSelector()).first();
                    if (titleElement != null) {
                        item.put("title", titleElement.text());
                    }
                }
                
                // 提取内容
                if (rule.getContentSelector() != null && !rule.getContentSelector().isEmpty()) {
                    Element contentElement = document.select(rule.getContentSelector()).first();
                    if (contentElement != null) {
                        item.put("content", contentElement.text());
                    }
                }
                
                // 提取日期
                if (rule.getDateSelector() != null && !rule.getDateSelector().isEmpty()) {
                    Element dateElement = document.select(rule.getDateSelector()).first();
                    if (dateElement != null) {
                        item.put("date", dateElement.text());
                    }
                }
                
                // 提取作者
                if (rule.getAuthorSelector() != null && !rule.getAuthorSelector().isEmpty()) {
                    Element authorElement = document.select(rule.getAuthorSelector()).first();
                    if (authorElement != null) {
                        item.put("author", authorElement.text());
                    }
                }
                
                if (!item.isEmpty()) {
                    results.add(item);
                }
            }
            
            // 转换为JSON
            return objectMapper.writeValueAsString(results);
        } catch (IOException e) {
            log.error("爬取内容失败，规则：{}，错误：{}", rule.getRuleName(), e.getMessage(), e);
            return "[]";
        }
    }

    @Override
    public String getHotspotData(String platform, int limit) {
        try {
            log.info("开始获取热点数据，平台：{}，限制数量：{}", platform, limit);
            
            String url;
            String selector;
            
            // 根据平台选择URL和选择器
            switch (platform.toLowerCase()) {
                case "baidu":
                    url = "https://top.baidu.com/board?tab=realtime";
                    selector = ".c-single-text-ellipsis";
                    break;
                case "toutiao":
                    url = "https://www.toutiao.com/hot-event/hot-board/?origin=toutiao_pc";
                    selector = ".title-content";
                    break;
                case "zhihu":
                    url = "https://www.zhihu.com/hot";
                    selector = ".HotItem-title";
                    break;
                default:
                    throw new IllegalArgumentException("不支持的平台：" + platform);
            }
            
            // 爬取页面
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();
            
            // 提取热点
            Elements elements = document.select(selector);
            List<String> hotspots = new ArrayList<>();
            
            int count = 0;
            for (Element element : elements) {
                if (count >= limit) {
                    break;
                }
                hotspots.add(element.text());
                count++;
            }
            
            // 转换为JSON
            Map<String, Object> result = new HashMap<>();
            result.put("platform", platform);
            result.put("hotspots", hotspots);
            
            return objectMapper.writeValueAsString(result);
        } catch (IOException e) {
            log.error("获取热点数据失败，平台：{}，错误：{}", platform, e.getMessage(), e);
            return "{}";
        }
    }

    @Override
    public String searchContent(String keyword, int limit) {
        try {
            log.info("开始搜索内容，关键词：{}，限制数量：{}", keyword, limit);
            
            // TODO: 实现Bing Search API调用
            // 这里是示例实现，实际项目中需要调用Bing Search API
            
            // 模拟搜索结果
            List<Map<String, String>> results = new ArrayList<>();
            for (int i = 0; i < limit; i++) {
                Map<String, String> item = new HashMap<>();
                item.put("title", "搜索结果 " + (i + 1) + " 的标题");
                item.put("snippet", "搜索结果 " + (i + 1) + " 的摘要，包含关键词 " + keyword);
                item.put("url", "https://example.com/result" + (i + 1));
                results.add(item);
            }
            
            // 转换为JSON
            Map<String, Object> result = new HashMap<>();
            result.put("keyword", keyword);
            result.put("results", results);
            
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            log.error("搜索内容失败，关键词：{}，错误：{}", keyword, e.getMessage(), e);
            return "{}";
        }
    }
    
    /**
     * 解析请求头
     */
    private Map<String, String> parseHeaders(String headersJson) {
        Map<String, String> headers = new HashMap<>();
        
        // 添加默认请求头
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        
        // 如果有自定义请求头，则解析并添加
        if (headersJson != null && !headersJson.isEmpty()) {
            try {
                Map<String, String> customHeaders = objectMapper.readValue(headersJson, Map.class);
                headers.putAll(customHeaders);
            } catch (IOException e) {
                log.error("解析请求头失败，错误：{}", e.getMessage(), e);
            }
        }
        
        return headers;
    }
} 