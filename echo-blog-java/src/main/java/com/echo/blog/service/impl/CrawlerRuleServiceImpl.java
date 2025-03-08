package com.echo.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echo.blog.entity.CrawlerRule;
import com.echo.blog.mapper.CrawlerRuleMapper;
import com.echo.blog.service.CrawlerRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 爬虫规则服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerRuleServiceImpl extends ServiceImpl<CrawlerRuleMapper, CrawlerRule> implements CrawlerRuleService {

    private final CrawlerRuleMapper crawlerRuleMapper;

    @Override
    @Transactional
    public CrawlerRule createRule(CrawlerRule rule) {
        rule.setStatus("ENABLED")
            .setCreatedTime(LocalDateTime.now())
            .setUpdatedTime(LocalDateTime.now());
        save(rule);
        return rule;
    }

    @Override
    @Transactional
    public CrawlerRule updateRule(CrawlerRule rule) {
        rule.setUpdatedTime(LocalDateTime.now());
        updateById(rule);
        return rule;
    }

    @Override
    public List<CrawlerRule> getEnabledRules() {
        return crawlerRuleMapper.findEnabledRules();
    }

    @Override
    public Page<CrawlerRule> getRulesByUser(String userId, Page<CrawlerRule> page) {
        LambdaQueryWrapper<CrawlerRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CrawlerRule::getCreatedBy, userId)
                   .orderByDesc(CrawlerRule::getCreatedTime);
        return page(page, queryWrapper);
    }

    @Override
    public CrawlerRule getRuleById(Long ruleId) {
        return getById(ruleId);
    }

    @Override
    @Transactional
    public CrawlerRule updateRuleStatus(Long ruleId, String status) {
        CrawlerRule rule = getById(ruleId);
        if (rule == null) {
            return null;
        }
        
        rule.setStatus(status)
            .setUpdatedTime(LocalDateTime.now());
        updateById(rule);
        return rule;
    }

    @Override
    @Transactional
    public boolean deleteRule(Long ruleId) {
        return removeById(ruleId);
    }
} 