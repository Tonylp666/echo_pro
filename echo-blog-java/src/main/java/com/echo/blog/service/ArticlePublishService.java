package com.echo.blog.service;

import com.echo.blog.entity.Article;
import com.echo.blog.model.dto.ArticlePublishDTO;
import com.echo.blog.model.vo.ArticlePublishResultVO;

import java.util.List;
import java.util.Map;

/**
 * 文章发布服务接口
 */
public interface ArticlePublishService {
    
    /**
     * 发布文章到平台
     *
     * @param publishDTO 发布信息
     * @param username 用户名
     * @return 发布结果
     */
    ArticlePublishResultVO publishArticle(ArticlePublishDTO publishDTO, String username);
    
    /**
     * 发布文章到指定平台
     * @param articleId 文章ID
     * @param platformTypes 平台类型列表
     * @return 发布结果
     */
    Map<String, Boolean> publishArticle(Long articleId, List<String> platformTypes);
    
    /**
     * 发布文章到所有已配置平台
     * @param articleId 文章ID
     * @return 发布结果
     */
    Map<String, Boolean> publishArticleToAllPlatforms(Long articleId);
    
    /**
     * 获取文章在各平台的发布状态
     * @param articleId 文章ID
     * @return 发布状态
     */
    Map<String, String> getArticlePublishStatus(Long articleId);
} 