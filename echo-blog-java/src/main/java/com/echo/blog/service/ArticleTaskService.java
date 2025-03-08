package com.echo.blog.service;

import com.echo.blog.entity.ArticleTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleTaskService {
    ArticleTask createTask(ArticleTask task);
    ArticleTask updateTask(ArticleTask task);
    void deleteTask(Long id);
    ArticleTask getTaskById(Long id);
    Page<ArticleTask> getAllTasks(Pageable pageable);
    Page<ArticleTask> getTasksByUser(String username, Pageable pageable);
    void executeTask(Long id);
    void scheduleTask(Long id);
    void unscheduleTask(Long id);
    void updateNextExecuteTime(Long id);
}