package com.echo.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.echo.blog.entity.Article;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章Mapper接口
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    
    /**
     * 搜索文章
     * @param keyword 关键词
     * @param page 分页参数
     * @return 文章分页列表
     */
    @Select("SELECT * FROM a_article WHERE title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%') ORDER BY created_time DESC")
    Page<Article> searchArticles(@Param("keyword") String keyword, Page<Article> page);
    
    /**
     * 根据分类获取文章
     * @param categoryId 分类ID
     * @param page 分页参数
     * @return 文章分页列表
     */
    @Select("SELECT * FROM a_article WHERE article_category_id = #{categoryId} ORDER BY created_time DESC")
    Page<Article> findByArticleCategoryId(@Param("categoryId") Long categoryId, Page<Article> page);
    
    /**
     * 获取用户的文章
     * @param userId 用户ID
     * @param page 分页参数
     * @return 文章分页列表
     */
    @Select("SELECT * FROM a_article WHERE created_by = #{userId} ORDER BY created_time DESC")
    Page<Article> findByCreatedBy(@Param("userId") String userId, Page<Article> page);
    
    /**
     * 获取浏览量最高的文章
     * @param limit 限制数量
     * @return 文章列表
     */
    @Select("SELECT * FROM a_article WHERE status = 'PUBLISHED' ORDER BY view_count DESC LIMIT #{limit}")
    Page<Article> findMostViewed(@Param("limit") int limit, Page<Article> page);
    
    @Select("SELECT * FROM a_article WHERE article_category_id = #{categoryId} AND status = 'PUBLISHED' ORDER BY created_time DESC")
    List<Article> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Select("SELECT * FROM a_article WHERE status = 'PUBLISHED' ORDER BY view_count DESC LIMIT #{limit}")
    List<Article> findMostViewed(@Param("limit") int limit);
    
    /**
     * 使用全文搜索查询文章
     * @param keyword 关键词
     * @param page 分页参数
     * @return 文章分页列表
     */
    @Select("SELECT * FROM a_article WHERE MATCH(title, content) AGAINST(#{keyword} IN BOOLEAN MODE) AND status = 'PUBLISHED'")
    Page<Article> searchArticlesByFullText(@Param("keyword") String keyword, Page<Article> page);
    
    /**
     * 根据标题或内容模糊搜索已发布文章
     * @param keyword 关键词
     * @param page 分页参数
     * @return 文章分页列表
     */
    @Select("SELECT * FROM a_article WHERE title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%') AND status = 'PUBLISHED'")
    IPage<Article> searchArticles(IPage<Article> page, @Param("keyword") String keyword);
}