package com.echo.blog.service;

import com.echo.blog.entity.ArticleTask;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 任务服务接口
 */
public interface TaskService extends IService<ArticleTask> {
    
    /**
     * 创建任务
     * @param task 任务信息
     * @return 创建的任务
     */
    ArticleTask createTask(ArticleTask task);
    
    /**
     * 更新任务
     * @param task 任务信息
     * @return 更新后的任务
     */
    ArticleTask updateTask(ArticleTask task);
    
    /**
     * 获取任务详情
     * @param id 任务ID
     * @return 任务详情
     */
    ArticleTask getTaskById(Long id);
    
    /**
     * 获取用户的任务
     * @param userId 用户ID
     * @param page 分页参数
     * @return 任务分页列表
     */
    Page<ArticleTask> getTasksByUser(String userId, Page<ArticleTask> page);
    
    /**
     * 更新任务状态
     * @param id 任务ID
     * @param status 状态
     * @return 更新后的任务
     */
    ArticleTask updateTaskStatus(Long id, String status);
    
    /**
     * 删除任务
     * @param id 任务ID
     * @return 是否成功
     */
    boolean deleteTask(Long id);
    
    /**
     * 检查用户是否有权限操作任务
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean checkTaskPermission(Long taskId, String userId);
} 