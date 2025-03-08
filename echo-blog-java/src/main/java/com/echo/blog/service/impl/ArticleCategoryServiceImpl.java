package com.echo.blog.service.impl;

import com.echo.blog.entity.ArticleCategory;
import com.echo.blog.mapper.ArticleCategoryMapper;
import com.echo.blog.service.ArticleCategoryService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    private final ArticleCategoryMapper articleCategoryMapper;

    @Override
    @Transactional
    public ArticleCategory createCategory(ArticleCategory category) {
        category.setCreatedTime(LocalDateTime.now())
               .setUpdatedTime(LocalDateTime.now());
        articleCategoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional

    public ArticleCategory updateCategory(ArticleCategory category) {
        category.setUpdatedTime(LocalDateTime.now());
        articleCategoryMapper.updateById(category);
        return category;
    }

    @Override
    @Transactional

    public void deleteCategory(Long id) {
        articleCategoryMapper.deleteById(id);
    }

    @Override

    public ArticleCategory getCategoryById(Long id) {
        return articleCategoryMapper.selectById(id);
    }

    @Override
    public Page<ArticleCategory> getAllCategories(Pageable pageable) {
        List<ArticleCategory> categories = articleCategoryMapper.selectList(null);
        return new PageImpl<>(categories, pageable, categories.size());
    }

    @Override
    public Page<ArticleCategory> getCategoriesByCreator(String createdBy, Pageable pageable) {
        List<ArticleCategory> categories = articleCategoryMapper.findByCreatedBy(createdBy);
        return new PageImpl<>(categories, pageable, categories.size());
    }

    @Override
    public boolean existsByName(String name) {
        return articleCategoryMapper.countByName(name) > 0;
    }
}