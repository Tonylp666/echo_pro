package com.echo.blog.service;

import com.echo.blog.model.dto.CrawlerRuleDTO;
import com.echo.blog.model.vo.CrawlerDataVO;
import com.echo.blog.model.vo.CrawlerRuleVO;

import java.util.List;

/**
 * 爬虫服务接口
 */
public interface CrawlerService {
    
    /**
     * 获取用户的爬虫规则列表
     *
     * @param username 用户名
     * @return 爬虫规则列表
     */
    List<CrawlerRuleVO> getRulesByUser(String username);
    
    /**
     * 创建爬虫规则
     *
     * @param ruleDTO 规则信息
     * @param username 用户名
     * @return 规则信息
     */
    CrawlerRuleVO createRule(CrawlerRuleDTO ruleDTO, String username);
    
    /**
     * 获取爬虫规则详情
     *
     * @param ruleId 规则ID
     * @return 规则详情
     */
    CrawlerRuleVO getRuleById(Long ruleId);
    
    /**
     * 更新爬虫规则
     *
     * @param ruleId 规则ID
     * @param ruleDTO 规则信息
     * @return 规则信息
     */
    CrawlerRuleVO updateRule(Long ruleId, CrawlerRuleDTO ruleDTO);
    
    /**
     * 更新爬虫规则状态
     *
     * @param ruleId 规则ID
     * @param status 状态
     * @return 规则信息
     */
    CrawlerRuleVO updateRuleStatus(Long ruleId, String status);
    
    /**
     * 删除爬虫规则
     *
     * @param ruleId 规则ID
     * @return 是否成功
     */
    boolean deleteRule(Long ruleId);
    
    /**
     * 测试爬虫规则
     *
     * @param ruleId 规则ID
     * @return 爬取的数据
     */
    List<CrawlerDataVO> testRule(Long ruleId);
    
    /**
     * 获取爬虫数据
     *
     * @param ruleId 规则ID
     * @return 爬取的数据
     */
    List<CrawlerDataVO> getData(Long ruleId);
    
    /**
     * 爬取内容
     *
     * @param rule 爬虫规则
     * @return 爬取的数据
     */
    String crawlContent(com.echo.blog.entity.CrawlerRule rule);
    
    /**
     * 获取热点数据
     *
     * @param platform 平台
     * @param limit 限制数量
     * @return 热点数据
     */
    String getHotspotData(String platform, int limit);
    
    /**
     * 搜索内容
     *
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 搜索结果
     */
    String searchContent(String keyword, int limit);
} 