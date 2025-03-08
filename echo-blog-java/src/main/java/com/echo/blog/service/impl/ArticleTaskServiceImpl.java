package com.echo.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echo.blog.entity.Article;
import com.echo.blog.entity.ArticleTask;
import com.echo.blog.mapper.ArticleTaskMapper;
import com.echo.blog.service.ArticleService;
import com.echo.blog.service.ArticleTaskService;
import com.echo.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ArticleTaskServiceImpl implements ArticleTaskService {

    private final ArticleTaskMapper articleTaskMapper;
    private final ArticleService articleService;
    private final UserService userService;
    private final TaskScheduler taskScheduler;

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public ArticleTask createTask(ArticleTask task) {
        task.setCreatedBy(userService.getCurrentUser().getName())
                .setCreatedTime(LocalDateTime.now())
                .setUpdatedTime(LocalDateTime.now())
                .setStatus("ACTIVE");

        if ("SCHEDULED".equals(task.getScheduleType()) && task.getScheduleCron() != null) {
            task.setNextExecuteTime(calculateNextExecuteTime(task.getScheduleCron()));
        }

        articleTaskMapper.insert(task);
        
        if ("SCHEDULED".equals(task.getScheduleType())) {
            scheduleTask(task.getId());
        }

        return task;
    }

    @Override
    @Transactional
    public ArticleTask updateTask(ArticleTask task) {
        ArticleTask existingTask = getTaskById(task.getId());
        
        if (!existingTask.getCreatedBy().equals(userService.getCurrentUser().getName())) {
            throw new RuntimeException("无权修改该任务");
        }

        task.setUpdatedTime(LocalDateTime.now());
        
        if ("SCHEDULED".equals(task.getScheduleType()) && task.getScheduleCron() != null) {
            task.setNextExecuteTime(calculateNextExecuteTime(task.getScheduleCron()));
            unscheduleTask(task.getId());
            scheduleTask(task.getId());
        }

        articleTaskMapper.updateById(task);
        return task;
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        ArticleTask task = getTaskById(id);
        if (!task.getCreatedBy().equals(userService.getCurrentUser().getName())) {
            throw new RuntimeException("无权删除该任务");
        }

        unscheduleTask(id);
        articleTaskMapper.deleteById(id);
    }

    @Override
    public ArticleTask getTaskById(Long id) {
        ArticleTask task = articleTaskMapper.selectById(id);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        return task;
    }

    @Override
    public org.springframework.data.domain.Page<ArticleTask> getAllTasks(Pageable pageable) {
        Page<ArticleTask> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        return convertToSpringPage(articleTaskMapper.selectPage(page, new LambdaQueryWrapper<ArticleTask>()
                .orderByDesc(ArticleTask::getCreatedTime)));
    }

    @Override
    public org.springframework.data.domain.Page<ArticleTask> getTasksByUser(String username, Pageable pageable) {
        Page<ArticleTask> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        return convertToSpringPage(articleTaskMapper.selectPage(page, new LambdaQueryWrapper<ArticleTask>()
                .eq(ArticleTask::getCreatedBy, username)
                .orderByDesc(ArticleTask::getCreatedTime)));
    }

    @Override
    @Transactional
    public void executeTask(Long id) {
        ArticleTask task = getTaskById(id);
        try {
            // TODO: 调用AI服务生成文章内容
            String generatedContent = "AI生成的文章内容";

            Article article = new Article()
                    .setArticleCategoryId(task.getArticleCategoryId())
                    .setTitle(task.getTitle())
                    .setContent(generatedContent);

            articleService.createArticle(article);

            task.setLastExecuteTime(LocalDateTime.now());
            if ("SCHEDULED".equals(task.getScheduleType())) {
                task.setNextExecuteTime(calculateNextExecuteTime(task.getScheduleCron()));
            }
            articleTaskMapper.updateById(task);
        } catch (Exception e) {
            task.setErrorMessage(e.getMessage());
            articleTaskMapper.updateById(task);
            throw e;
        }
    }

    @Override
    public void scheduleTask(Long id) {
        ArticleTask task = getTaskById(id);
        if (!"SCHEDULED".equals(task.getScheduleType()) || task.getScheduleCron() == null) {
            return;
        }

        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(
                () -> executeTask(id),
                new CronTrigger(task.getScheduleCron())
        );

        scheduledTasks.put(id, scheduledFuture);
    }

    @Override
    public void unscheduleTask(Long id) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.remove(id);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    @Override
    @Transactional
    public void updateNextExecuteTime(Long id) {
        ArticleTask task = getTaskById(id);
        if ("SCHEDULED".equals(task.getScheduleType()) && task.getScheduleCron() != null) {
            task.setNextExecuteTime(calculateNextExecuteTime(task.getScheduleCron()));
            articleTaskMapper.updateById(task);
        }
    }

    private LocalDateTime calculateNextExecuteTime(String cronExpression) {
        // TODO: 根据cron表达式计算下次执行时间
        return LocalDateTime.now().plusHours(1);
    }

    private org.springframework.data.domain.Page<ArticleTask> convertToSpringPage(Page<ArticleTask> mybatisPage) {
        return new org.springframework.data.domain.PageImpl<>(
                mybatisPage.getRecords(),
                org.springframework.data.domain.PageRequest.of(
                        (int) (mybatisPage.getCurrent() - 1),
                        (int) mybatisPage.getSize()),
                mybatisPage.getTotal());
    }
}