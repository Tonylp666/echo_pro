package com.echo.blog.controller;

import com.echo.blog.common.api.ApiResult;
import com.echo.blog.entity.Article;
import com.echo.blog.service.ArticleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 文章控制器
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    
    private final ArticleService articleService;
    
    /**
     * 创建文章
     *
     * @param article 文章信息
     * @param authentication 认证信息
     * @return 创建的文章
     */
    @PostMapping
    public ApiResult<Article> createArticle(@RequestBody Article article, Authentication authentication) {
        article.setCreatedBy(authentication.getName());
        return ApiResult.success(articleService.createArticle(article));
    }
    
    /**
     * 更新文章
     *
     * @param id 文章ID
     * @param article 文章信息
     * @return 更新后的文章
     */
    @PutMapping("/{id}")
    public ApiResult<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        article.setId(id);
        return ApiResult.success(articleService.updateArticle(article));
    }
    
    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public ApiResult<Article> getArticle(@PathVariable Long id) {
        return ApiResult.success(articleService.getArticleById(id));
    }
    
    /**
     * 搜索文章
     *
     * @param keyword 关键词
     * @param current 当前页
     * @param size 每页大小
     * @return 文章分页列表
     */
    @GetMapping("/search")
    public ApiResult<Page<Article>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Article> page = new Page<>(current, size);
        return ApiResult.success(articleService.searchArticles(keyword, page));
    }
    
    /**
     * 根据分类获取文章
     *
     * @param categoryId 分类ID
     * @param current 当前页
     * @param size 每页大小
     * @return 文章分页列表
     */
    @GetMapping("/category/{categoryId}")
    public ApiResult<Page<Article>> getArticlesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Article> page = new Page<>(current, size);
        return ApiResult.success(articleService.getArticlesByCategory(categoryId, page));
    }
    
    /**
     * 获取用户的文章
     *
     * @param userId 用户ID
     * @param current 当前页
     * @param size 每页大小
     * @return 文章分页列表
     */
    @GetMapping("/user/{userId}")
    public ApiResult<Page<Article>> getArticlesByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Article> page = new Page<>(current, size);
        return ApiResult.success(articleService.getArticlesByUser(userId, page));
    }
    
    /**
     * 更新文章状态
     *
     * @param id 文章ID
     * @param status 状态
     * @return 更新后的文章
     */
    @PutMapping("/{id}/status")
    public ApiResult<Article> updateArticleStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ApiResult.success(articleService.updateArticleStatus(id, status));
    }
    
    /**
     * 增加文章浏览次数
     *
     * @param id 文章ID
     * @return 是否成功
     */
    @PutMapping("/{id}/view")
    public ApiResult<Boolean> incrementViewCount(@PathVariable Long id) {
        return ApiResult.success(articleService.incrementViewCount(id));
    }
    
    /**
     * 增加文章点赞次数
     *
     * @param id 文章ID
     * @return 是否成功
     */
    @PutMapping("/{id}/like")
    public ApiResult<Boolean> incrementLikeCount(@PathVariable Long id) {
        return ApiResult.success(articleService.incrementLikeCount(id));
    }
    
    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public ApiResult<Boolean> deleteArticle(@PathVariable Long id) {
        return ApiResult.success(articleService.deleteArticle(id));
    }
}