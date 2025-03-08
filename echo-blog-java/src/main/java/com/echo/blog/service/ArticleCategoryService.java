package com.echo.blog.service;

import com.echo.blog.entity.ArticleCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleCategoryService {
    
    ArticleCategory createCategory(ArticleCategory category);
    
    ArticleCategory updateCategory(ArticleCategory category);
    
    void deleteCategory(Long id);
    
    ArticleCategory getCategoryById(Long id);
    
    Page<ArticleCategory> getAllCategories(Pageable pageable);
    
    Page<ArticleCategory> getCategoriesByCreator(String createdBy, Pageable pageable);
    
    boolean existsByName(String name);
}