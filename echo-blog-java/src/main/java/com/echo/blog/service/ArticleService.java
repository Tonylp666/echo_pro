package com.echo.blog.service;

import com.echo.blog.entity.Article;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 文章服务接口
 */
public interface ArticleService extends IService<Article> {
    
    /**
     * 创建文章
     * @param article 文章信息
     * @return 创建的文章
     */
    Article createArticle(Article article);
    
    /**
     * 更新文章
     * @param article 文章信息
     * @return 更新后的文章
     */
    Article updateArticle(Article article);
    
    /**
     * 获取文章详情
     * @param id 文章ID
     * @return 文章详情
     */
    Article getArticleById(Long id);
    
    /**
     * 搜索文章
     * @param keyword 关键词
     * @param page 分页参数
     * @return 文章分页列表
     */
    Page<Article> searchArticles(String keyword, Page<Article> page);
    
    /**
     * 根据分类获取文章
     * @param categoryId 分类ID
     * @param page 分页参数
     * @return 文章分页列表
     */
    Page<Article> getArticlesByCategory(Long categoryId, Page<Article> page);
    
    /**
     * 获取用户的文章
     * @param userId 用户ID
     * @param page 分页参数
     * @return 文章分页列表
     */
    Page<Article> getArticlesByUser(String userId, Page<Article> page);
    
    /**
     * 更新文章状态
     * @param id 文章ID
     * @param status 状态
     * @return 更新后的文章
     */
    Article updateArticleStatus(Long id, String status);
    
    /**
     * 增加文章浏览次数
     * @param id 文章ID
     * @return 是否成功
     */
    boolean incrementViewCount(Long id);
    
    /**
     * 增加文章点赞次数
     * @param id 文章ID
     * @return 是否成功
     */
    boolean incrementLikeCount(Long id);
    
    /**
     * 删除文章
     * @param id 文章ID
     * @return 是否成功
     */
    boolean deleteArticle(Long id);
    
    /**
     * 检查用户是否有权限操作文章
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean checkArticlePermission(Long articleId, String userId);
}