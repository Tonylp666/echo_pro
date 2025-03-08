package com.echo.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echo.blog.common.api.ApiResult;
import com.echo.blog.entity.CrawlerRule;
import com.echo.blog.model.dto.CrawlerRuleDTO;
import com.echo.blog.model.vo.CrawlerRuleVO;
import com.echo.blog.security.RequirePermission;
import com.echo.blog.service.CrawlerRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 爬虫规则控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/crawler-rules")
@RequiredArgsConstructor
@Tag(name = "爬虫规则", description = "爬虫规则相关接口")
public class CrawlerRuleController {

    private final CrawlerRuleService crawlerRuleService;

    @PostMapping
    @Operation(summary = "创建爬虫规则")
    @RequirePermission("crawler:rule:create")
    public ApiResult<CrawlerRuleVO> createRule(@RequestBody CrawlerRuleDTO ruleDTO, Authentication authentication) {
        CrawlerRule rule = new CrawlerRule();
        BeanUtils.copyProperties(ruleDTO, rule);
        rule.setCreatedBy(authentication.getName());
        
        CrawlerRule createdRule = crawlerRuleService.createRule(rule);
        CrawlerRuleVO ruleVO = convertToVO(createdRule);
        
        return ApiResult.success(ruleVO);
    }

    @GetMapping
    @Operation(summary = "获取当前用户的爬虫规则列表")
    @RequirePermission("crawler:rule:list")
    public ApiResult<Page<CrawlerRuleVO>> getRuleList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        
        Page<CrawlerRule> page = new Page<>(current, size);
        Page<CrawlerRule> rulePage = crawlerRuleService.getRulesByUser(authentication.getName(), page);
        
        List<CrawlerRuleVO> ruleVOList = rulePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        Page<CrawlerRuleVO> resultPage = new Page<>(rulePage.getCurrent(), rulePage.getSize(), rulePage.getTotal());
        resultPage.setRecords(ruleVOList);
        
        return ApiResult.success(resultPage);
    }

    @GetMapping("/enabled")
    @Operation(summary = "获取所有启用的爬虫规则")
    @RequirePermission("crawler:rule:list")
    public ApiResult<List<CrawlerRuleVO>> getEnabledRules() {
        List<CrawlerRule> rules = crawlerRuleService.getEnabledRules();
        List<CrawlerRuleVO> ruleVOList = rules.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return ApiResult.success(ruleVOList);
    }

    @GetMapping("/{ruleId}")
    @Operation(summary = "获取爬虫规则详情")
    @RequirePermission("crawler:rule:view")
    public ApiResult<CrawlerRuleVO> getRuleDetail(@PathVariable Long ruleId) {
        CrawlerRule rule = crawlerRuleService.getRuleById(ruleId);
        if (rule == null) {
            return ApiResult.failed("规则不存在");
        }
        
        CrawlerRuleVO ruleVO = convertToVO(rule);
        return ApiResult.success(ruleVO);
    }

    @PutMapping("/{ruleId}")
    @Operation(summary = "更新爬虫规则")
    @RequirePermission("crawler:rule:update")
    public ApiResult<CrawlerRuleVO> updateRule(@PathVariable Long ruleId, @RequestBody CrawlerRuleDTO ruleDTO) {
        CrawlerRule rule = crawlerRuleService.getRuleById(ruleId);
        if (rule == null) {
            return ApiResult.failed("规则不存在");
        }
        
        BeanUtils.copyProperties(ruleDTO, rule);
        CrawlerRule updatedRule = crawlerRuleService.updateRule(rule);
        CrawlerRuleVO ruleVO = convertToVO(updatedRule);
        
        return ApiResult.success(ruleVO);
    }

    @PutMapping("/{ruleId}/status")
    @Operation(summary = "更新爬虫规则状态")
    @RequirePermission("crawler:rule:update")
    public ApiResult<CrawlerRuleVO> updateRuleStatus(@PathVariable Long ruleId, @RequestParam String status) {
        CrawlerRule updatedRule = crawlerRuleService.updateRuleStatus(ruleId, status);
        if (updatedRule == null) {
            return ApiResult.failed("规则不存在");
        }
        
        CrawlerRuleVO ruleVO = convertToVO(updatedRule);
        return ApiResult.success(ruleVO);
    }

    @DeleteMapping("/{ruleId}")
    @Operation(summary = "删除爬虫规则")
    @RequirePermission("crawler:rule:delete")
    public ApiResult<Boolean> deleteRule(@PathVariable Long ruleId) {
        boolean result = crawlerRuleService.deleteRule(ruleId);
        return result ? ApiResult.success(true) : ApiResult.failed("删除失败");
    }

    /**
     * 将实体转换为VO
     */
    private CrawlerRuleVO convertToVO(CrawlerRule rule) {
        CrawlerRuleVO vo = new CrawlerRuleVO();
        BeanUtils.copyProperties(rule, vo);
        return vo;
    }
} 