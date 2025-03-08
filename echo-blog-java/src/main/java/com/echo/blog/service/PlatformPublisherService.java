package com.echo.blog.service;

import com.echo.blog.entity.Article;
import com.echo.blog.entity.PlatformConfig;
import com.echo.blog.entity.UserPlatformAuth;

/**
 * 平台发布服务接口
 */
public interface PlatformPublisherService {
    
    /**
     * 发布文章
     *
     * @param article 文章
     * @param auth 用户平台授权
     * @param platformConfig 平台配置
     * @return 平台文章ID
     * @throws Exception 发布异常
     */
    String publishArticle(Article article, UserPlatformAuth auth, PlatformConfig platformConfig) throws Exception;
}