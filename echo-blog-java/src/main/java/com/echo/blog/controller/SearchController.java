package com.echo.blog.controller;

import com.echo.blog.common.api.ApiResult;
import com.echo.blog.model.vo.SearchResultVO;
import com.echo.blog.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "搜索", description = "搜索相关接口")
public class SearchController {
    
    private final SearchService searchService;
    
    /**
     * 搜索
     *
     * @param keywords 关键词
     * @param count 结果数量
     * @return 搜索结果
     */
    @GetMapping
    @Operation(summary = "搜索")
    public ApiResult<List<SearchResultVO>> search(
            @RequestParam String keywords,
            @RequestParam(required = false, defaultValue = "10") int count) {
        return ApiResult.success(searchService.search(keywords, count));
    }
} 