package com.echo.blog.controller;

import com.echo.blog.common.api.ApiResult;
import com.echo.blog.model.dto.ArticlePublishDTO;
import com.echo.blog.model.vo.ArticlePublishResultVO;
import com.echo.blog.service.ArticlePublishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * 文章发布控制器
 */
@RestController
@RequestMapping("/api/article/publish")
@RequiredArgsConstructor
@Tag(name = "文章发布", description = "文章发布相关接口")
public class ArticlePublishController {
    
    private final ArticlePublishService articlePublishService;
    
    /**
     * 发布文章到平台
     *
     * @param publishDTO 发布信息
     * @param authentication 认证信息
     * @return 发布结果
     */
    @PostMapping
    @Operation(summary = "发布文章到平台")
    public ApiResult<ArticlePublishResultVO> publishArticle(
            @RequestBody @Valid ArticlePublishDTO publishDTO,
            Authentication authentication) {
        String username = authentication.getName();
        ArticlePublishResultVO result = articlePublishService.publishArticle(publishDTO, username);
        return ApiResult.success(result);
    }
} 