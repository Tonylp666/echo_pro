package com.echo.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.echo.blog.entity.ArticleSub;

import java.util.List;

/**
 * 文章子表服务接口
 */
public interface ArticleSubService extends IService<ArticleSub> {
    
    /**
     * 根据文章ID获取子表记录
     * @param articleId 文章ID
     * @return 子表记录列表
     */
    List<ArticleSub> getByArticleId(Long articleId);
    
    /**
     * 根据文章ID和子类型获取子表记录
     * @param articleId 文章ID
     * @param subType 子类型
     * @return 子表记录
     */
    ArticleSub getByArticleIdAndSubType(Long articleId, String subType);
} 