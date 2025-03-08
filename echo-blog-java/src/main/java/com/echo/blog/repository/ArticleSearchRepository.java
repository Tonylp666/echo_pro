package com.echo.blog.repository;

import com.echo.blog.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleSearchRepository extends JpaRepository<Article, Long> {
}