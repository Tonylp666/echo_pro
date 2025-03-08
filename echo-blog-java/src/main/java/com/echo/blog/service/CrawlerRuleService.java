package com.echo.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echo.blog.entity.CrawlerRule;

import java.util.List;

/**
 * 爬虫规则服务接口
 */
public interface CrawlerRuleService {
    
    /**
     * 创建爬虫规则
     * @param rule 规则信息
     * @return 创建的规则
     */
    CrawlerRule createRule(CrawlerRule rule);
    
    /**
     * 更新爬虫规则
     * @param rule 规则信息
     * @return 更新后的规则
     */
    CrawlerRule updateRule(CrawlerRule rule);
    
    /**
     * 获取启用的爬虫规则
     * @return 启用的爬虫规则列表
     */
    List<CrawlerRule> getEnabledRules();
    
    /**
     * 获取用户创建的爬虫规则
     * @param userId 用户ID
     * @param page 分页参数
     * @return 爬虫规则分页列表
     */
    Page<CrawlerRule> getRulesByUser(String userId, Page<CrawlerRule> page);
    
    /**
     * 获取爬虫规则详情
     * @param ruleId 规则ID
     * @return 规则详情
     */
    CrawlerRule getRuleById(Long ruleId);
    
    /**
     * 启用/禁用爬虫规则
     * @param ruleId 规则ID
     * @param status 状态
     * @return 更新后的规则
     */
    CrawlerRule updateRuleStatus(Long ruleId, String status);
    
    /**
     * 删除爬虫规则
     * @param ruleId 规则ID
     * @return 是否成功
     */
    boolean deleteRule(Long ruleId);
} 