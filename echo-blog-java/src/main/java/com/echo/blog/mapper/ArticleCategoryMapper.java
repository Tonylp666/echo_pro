package com.echo.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echo.blog.entity.ArticleCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleCategoryMapper extends BaseMapper<ArticleCategory> {
    @Select("SELECT * FROM article_category WHERE created_by = #{createdBy}")
    List<ArticleCategory> findByCreatedBy(String createdBy);

    @Select("SELECT COUNT(*) FROM article_category WHERE name = #{name}")
    int countByName(String name);
}