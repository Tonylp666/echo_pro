package com.echo.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echo.blog.entity.ArticleSub;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章子表Mapper接口
 */
@Mapper
public interface ArticleSubMapper extends BaseMapper<ArticleSub> {
    
    /**
     * 根据文章ID获取子表记录
     * @param articleId 文章ID
     * @return 子表记录列表
     */
    @Select("SELECT * FROM a_article_sub WHERE article_id = #{articleId}")
    List<ArticleSub> findByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 根据文章ID和子类型获取子表记录
     * @param articleId 文章ID
     * @param subType 子类型
     * @return 子表记录
     */
    @Select("SELECT * FROM a_article_sub WHERE article_id = #{articleId} AND sub_type = #{subType}")
    ArticleSub findByArticleIdAndSubType(@Param("articleId") Long articleId, @Param("subType") String subType);
    
    @Select("SELECT * FROM a_article_sub WHERE sync_status = #{status} ORDER BY created_time ASC")
    List<ArticleSub> findBySyncStatus(@Param("status") String status);
    
    @Select("SELECT * FROM a_article_sub WHERE sub_type = #{subType} AND sync_status = #{status}")
    List<ArticleSub> findBySubTypeAndStatus(@Param("subType") String subType, @Param("status") String status);
}