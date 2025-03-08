package com.echo.blog.controller;

import com.echo.blog.common.api.Result;
import com.echo.blog.entity.ArticleTask;
import com.echo.blog.service.ArticleTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "文章自动生成任务", description = "文章自动生成任务相关接口")
@RestController
@RequestMapping("/api/article-tasks")
@RequiredArgsConstructor
public class ArticleTaskController {

    private final ArticleTaskService articleTaskService;

    @Operation(summary = "创建任务")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<ArticleTask> createTask(@RequestBody @Valid ArticleTask task) {
        return Result.success(articleTaskService.createTask(task));
    }

    @Operation(summary = "更新任务")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<ArticleTask> updateTask(@PathVariable Long id, @RequestBody @Valid ArticleTask task) {
        task.setId(id);
        return Result.success(articleTaskService.updateTask(task));
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteTask(@PathVariable Long id) {
        articleTaskService.deleteTask(id);
        return Result.success();
    }

    @Operation(summary = "获取任务详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<ArticleTask> getTask(@PathVariable Long id) {
        return Result.success(articleTaskService.getTaskById(id));
    }

    @Operation(summary = "获取所有任务")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<ArticleTask>> getAllTasks(Pageable pageable) {
        return Result.success(articleTaskService.getAllTasks(pageable));
    }

    @Operation(summary = "获取用户的任务")
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<ArticleTask>> getMyTasks(Pageable pageable) {
        return Result.success(articleTaskService.getTasksByUser(
                articleTaskService.getTaskById(1L).getCreatedBy(), pageable));
    }

    @Operation(summary = "执行任务")
    @PostMapping("/{id}/execute")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> executeTask(@PathVariable Long id) {
        articleTaskService.executeTask(id);
        return Result.success();
    }

    @Operation(summary = "启用定时任务")
    @PostMapping("/{id}/schedule")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> scheduleTask(@PathVariable Long id) {
        articleTaskService.scheduleTask(id);
        return Result.success();
    }

    @Operation(summary = "停用定时任务")
    @PostMapping("/{id}/unschedule")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> unscheduleTask(@PathVariable Long id) {
        articleTaskService.unscheduleTask(id);
        return Result.success();
    }
}