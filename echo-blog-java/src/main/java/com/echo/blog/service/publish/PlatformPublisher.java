package com.echo.blog.service.publish;

import com.echo.blog.entity.Article;
import com.echo.blog.exception.PublishException;
import org.springframework.web.multipart.MultipartFile;

public interface PlatformPublisher {
    String publishArticle(Article article) throws PublishException;
    String uploadMedia(MultipartFile file) throws PublishException;
}