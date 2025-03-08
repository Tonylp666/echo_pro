package com.echo.blog.controller;

import com.echo.blog.common.api.ApiResult;
import com.echo.blog.model.dto.CrawlerRuleDTO;
import com.echo.blog.model.vo.CrawlerDataVO;
import com.echo.blog.model.vo.CrawlerRuleVO;
import com.echo.blog.service.CrawlerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 爬虫控制器
 */
@RestController
@RequestMapping("/api/crawler")
@RequiredArgsConstructor
@Tag(name = "爬虫", description = "爬虫相关接口")
public class CrawlerController {
    
    private final CrawlerService crawlerService;
    
    /**
     * 获取爬虫规则列表
     *
     * @param authentication 认证信息
     * @return 爬虫规则列表
     */
    @GetMapping("/rules")
    @Operation(summary = "获取爬虫规则列表")
    public ApiResult<List<CrawlerRuleVO>> getRules(Authentication authentication) {
        String username = authentication.getName();
        List<CrawlerRuleVO> rules = crawlerService.getRulesByUser(username);
        return ApiResult.success(rules);
    }
    
    /**
     * 创建爬虫规则
     *
     * @param ruleDTO 规则信息
     * @param authentication 认证信息
     * @return 规则信息
     */
    @PostMapping("/rule")
    @Operation(summary = "创建爬虫规则")
    public ApiResult<CrawlerRuleVO> createRule(
            @RequestBody @Valid CrawlerRuleDTO ruleDTO,
            Authentication authentication) {
        String username = authentication.getName();
        CrawlerRuleVO ruleVO = crawlerService.createRule(ruleDTO, username);
        return ApiResult.success(ruleVO);
    }
    
    /**
     * 获取爬虫规则详情
     *
     * @param ruleId 规则ID
     * @return 规则详情
     */
    @GetMapping("/rule/{ruleId}")
    @Operation(summary = "获取爬虫规则详情")
    public ApiResult<CrawlerRuleVO> getRuleDetail(@PathVariable Long ruleId) {
        CrawlerRuleVO ruleVO = crawlerService.getRuleById(ruleId);
        return ApiResult.success(ruleVO);
    }
    
    /**
     * 更新爬虫规则
     *
     * @param ruleId 规则ID
     * @param ruleDTO 规则信息
     * @return 规则信息
     */
    @PutMapping("/rule/{ruleId}")
    @Operation(summary = "更新爬虫规则")
    public ApiResult<CrawlerRuleVO> updateRule(
            @PathVariable Long ruleId,
            @RequestBody @Valid CrawlerRuleDTO ruleDTO) {
        CrawlerRuleVO ruleVO = crawlerService.updateRule(ruleId, ruleDTO);
        return ApiResult.success(ruleVO);
    }
    
    /**
     * 更新爬虫规则状态
     *
     * @param ruleId 规则ID
     * @param status 状态
     * @return 规则信息
     */
    @PatchMapping("/rule/{ruleId}/status")
    @Operation(summary = "更新爬虫规则状态")
    public ApiResult<CrawlerRuleVO> updateRuleStatus(
            @PathVariable Long ruleId,
            @RequestParam String status) {
        CrawlerRuleVO ruleVO = crawlerService.updateRuleStatus(ruleId, status);
        return ApiResult.success(ruleVO);
    }
    
    /**
     * 删除爬虫规则
     *
     * @param ruleId 规则ID
     * @return 是否成功
     */
    @DeleteMapping("/rule/{ruleId}")
    @Operation(summary = "删除爬虫规则")
    public ApiResult<Boolean> deleteRule(@PathVariable Long ruleId) {
        boolean result = crawlerService.deleteRule(ruleId);
        return ApiResult.success(result);
    }
    
    /**
     * 测试爬虫规则
     *
     * @param ruleId 规则ID
     * @return 爬取的数据
     */
    @PostMapping("/rule/{ruleId}/test")
    @Operation(summary = "测试爬虫规则")
    public ApiResult<List<CrawlerDataVO>> testRule(@PathVariable Long ruleId) {
        List<CrawlerDataVO> data = crawlerService.testRule(ruleId);
        return ApiResult.success(data);
    }
    
    /**
     * 获取爬虫数据
     *
     * @param ruleId 规则ID
     * @return 爬取的数据
     */
    @GetMapping("/data/{ruleId}")
    @Operation(summary = "获取爬虫数据")
    public ApiResult<List<CrawlerDataVO>> getData(@PathVariable Long ruleId) {
        List<CrawlerDataVO> data = crawlerService.getData(ruleId);
        return ApiResult.success(data);
    }
} 