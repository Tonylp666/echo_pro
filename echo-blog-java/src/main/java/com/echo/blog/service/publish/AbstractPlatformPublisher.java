package com.echo.blog.service.publish;

import com.echo.blog.entity.Article;
import com.echo.blog.entity.ArticleSub;
import com.echo.blog.entity.PlatformConfig;
import com.echo.blog.entity.UserPlatformAuth;
import com.echo.blog.enums.PlatformType;
import com.echo.blog.service.ArticleSubService;
import com.echo.blog.service.PlatformPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 平台发布服务抽象类
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPlatformPublisher implements PlatformPublisherService {
    
    protected final ArticleSubService articleSubService;
    
    @Override
    public String publishArticle(Article article, UserPlatformAuth auth, PlatformConfig platformConfig) throws Exception {
        try {
            // 1. 转换内容
            String platformContent = convertContent(article.getContentMarkdown());
            
            // 2. 发布到平台
            String platformArticleId = doPublish(article, platformContent, auth, platformConfig);
            
            // 3. 保存发布记录
            saveArticleSub(article, platformArticleId, platformConfig);
            
            return platformArticleId;
        } catch (Exception e) {
            log.error("发布文章到平台失败: {}", getPlatformType(), e);
            throw e;
        }
    }
    
    /**
     * 兼容旧代码的方法
     */
    public boolean publishArticle(Article article, PlatformConfig platformConfig) {
        try {
            // 这里简单实现，实际应该从数据库获取auth信息
            UserPlatformAuth auth = new UserPlatformAuth();
            auth.setUserId(Long.valueOf(article.getCreatedBy()));
            auth.setPlatformType(PlatformType.valueOf(platformConfig.getPlatformType()));
            
            String result = publishArticle(article, auth, platformConfig);
            return result != null && !result.isEmpty();
        } catch (Exception e) {
            log.error("发布文章到平台失败", e);
            return false;
        }
    }
    
    /**
     * 获取平台类型
     *
     * @return 平台类型
     */
    protected abstract String getPlatformType();
    
    /**
     * 转换内容
     *
     * @param markdown Markdown内容
     * @return 转换后的内容
     */
    protected abstract String convertContent(String markdown);
    
    /**
     * 执行发布
     *
     * @param article 文章
     * @param platformContent 平台内容
     * @param auth 用户平台授权
     * @param platformConfig 平台配置
     * @return 平台文章ID
     * @throws Exception 发布异常
     */
    protected abstract String doPublish(Article article, String platformContent, UserPlatformAuth auth, PlatformConfig platformConfig) throws Exception;
    
    /**
     * 保存文章子表
     *
     * @param article 文章
     * @param platformArticleId 平台文章ID
     * @param platformConfig 平台配置
     * @return 文章子表
     */
    private ArticleSub saveArticleSub(Article article, String platformArticleId, PlatformConfig platformConfig) {
        ArticleSub articleSub = new ArticleSub();
        articleSub.setArticleId(article.getId());
        articleSub.setSubType(getPlatformType());
        articleSub.setSubId(platformArticleId);
        articleSub.setSubUrl("");
        articleSub.setStatus("SUCCESS");
        articleSub.setPlatformMeta(platformConfig.getPlatformName());
        articleSub.setCreatedTime(LocalDateTime.now());
        articleSub.setUpdatedTime(LocalDateTime.now());
        
        articleSubService.save(articleSub);
        return articleSub;
    }
} 