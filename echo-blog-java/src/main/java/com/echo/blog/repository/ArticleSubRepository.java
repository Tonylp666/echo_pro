package com.echo.blog.repository;

import com.echo.blog.entity.ArticleSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章发布记录仓库接口
 */
@Repository
public interface ArticleSubRepository extends JpaRepository<ArticleSub, Long> {
    
    /**
     * 根据文章ID查询发布记录
     *
     * @param articleId 文章ID
     * @return 发布记录列表
     */
    List<ArticleSub> findByArticleId(Long articleId);
    
    /**
     * 根据文章ID和平台类型查询发布记录
     *
     * @param articleId 文章ID
     * @param subType 平台类型
     * @return 发布记录
     */
    ArticleSub findByArticleIdAndSubType(Long articleId, String subType);
    
    /**
     * 根据平台类型和平台文章ID查询发布记录
     *
     * @param subType 平台类型
     * @param subId 平台文章ID
     * @return 发布记录
     */
    ArticleSub findBySubTypeAndSubId(String subType, String subId);
} 