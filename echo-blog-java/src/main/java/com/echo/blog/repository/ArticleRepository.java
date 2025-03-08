package com.echo.blog.repository;

import com.echo.blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 文章仓库接口
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    /**
     * 根据创建者查询文章
     *
     * @param createdBy 创建者
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    Page<Article> findByCreatedBy(String createdBy, Pageable pageable);
    
    /**
     * 根据标题或内容模糊查询
     *
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword% OR a.content LIKE %:keyword%")
    Page<Article> findByTitleOrContentContaining(String keyword, Pageable pageable);
    
    /**
     * 根据分类ID查询文章
     *
     * @param categoryId 分类ID
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    Page<Article> findByCategoryId(Long categoryId, Pageable pageable);
    
    /**
     * 查询公开的文章
     *
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED'")
    Page<Article> findPublishedArticles(Pageable pageable);
    
    /**
     * 查询指定用户公开的文章
     *
     * @param createdBy 创建者
     * @param pageable 分页参数
     * @return 文章分页列表
     */
    @Query("SELECT a FROM Article a WHERE a.createdBy = :createdBy AND a.status = 'PUBLISHED'")
    Page<Article> findPublishedArticlesByUser(String createdBy, Pageable pageable);
} 