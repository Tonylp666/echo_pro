package com.echo.blog.service.impl;

import com.echo.blog.entity.Article;
import com.echo.blog.service.ArticleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echo.blog.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 文章服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    
    private final ArticleMapper articleMapper;
    
    @Override
    @Transactional
    public Article createArticle(Article article) {
        article.setCreatedTime(LocalDateTime.now());
        article.setUpdatedTime(LocalDateTime.now());
        save(article);
        return article;
    }
    
    @Override
    @Transactional
    public Article updateArticle(Article article) {
        article.setUpdatedTime(LocalDateTime.now());
        updateById(article);
        return article;
    }
    
    @Override
    @Transactional
    public boolean deleteArticle(Long id) {
        return removeById(id);
    }
    
    @Override
    public Article getArticleById(Long id) {
        return getById(id);
    }
    
    @Override
    public Page<Article> searchArticles(String keyword, Page<Article> page) {
        return articleMapper.searchArticles(keyword, page);
    }
    
    @Override
    public Page<Article> getArticlesByCategory(Long categoryId, Page<Article> page) {
        return articleMapper.findByArticleCategoryId(categoryId, page);
    }
    
    @Override
    public Page<Article> getArticlesByUser(String userId, Page<Article> page) {
        return articleMapper.findByCreatedBy(userId, page);
    }
    
    @Override
    @Transactional
    public Article updateArticleStatus(Long id, String status) {
        Article article = getById(id);
        if (article != null) {
            article.setStatus(status);
            article.setUpdatedTime(LocalDateTime.now());
            updateById(article);
        }
        return article;
    }
    
    @Override
    @Transactional
    public boolean incrementViewCount(Long id) {
        Article article = getById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            return updateById(article);
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean incrementLikeCount(Long id) {
        Article article = getById(id);
        if (article != null) {
            article.setLikeCount(article.getLikeCount() + 1);
            return updateById(article);
        }
        return false;
    }
    
    @Override
    public boolean checkArticlePermission(Long articleId, String userId) {
        Article article = getById(articleId);
        return article != null && article.getCreatedBy().equals(userId);
    }
}