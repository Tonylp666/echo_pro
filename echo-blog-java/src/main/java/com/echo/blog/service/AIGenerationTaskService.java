package com.echo.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echo.blog.entity.AIGenerationTask;
import com.echo.blog.model.dto.AIGenerationTaskDTO;
import com.echo.blog.model.vo.AIGenerationTaskVO;

import java.util.List;

/**
 * AI内容生成任务服务接口
 */
public interface AIGenerationTaskService {
    
    /**
     * 创建AI生成任务
     * @param task 任务信息
     * @return 创建的任务
     */
    AIGenerationTask createTask(AIGenerationTask task);
    
    /**
     * 创建AI生成任务（DTO转换）
     * @param taskDTO 任务DTO
     * @param username 用户名
     * @return 创建的任务VO
     */
    AIGenerationTaskVO createTask(AIGenerationTaskDTO taskDTO, String username);
    
    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 新状态
     * @param errorMessage 错误信息（如果有）
     * @return 更新后的任务
     */
    AIGenerationTask updateTaskStatus(Long taskId, String status, String errorMessage);
    
    /**
     * 更新任务结果
     * @param taskId 任务ID
     * @param articleId 生成的文章ID
     * @return 更新后的任务
     */
    AIGenerationTask updateTaskResult(Long taskId, Long articleId);
    
    /**
     * 获取待处理的任务
     * @param limit 限制数量
     * @return 待处理任务列表
     */
    List<AIGenerationTask> getPendingTasks(int limit);
    
    /**
     * 获取用户的任务列表
     * @param userId 用户ID
     * @param page 分页参数
     * @return 任务分页列表
     */
    Page<AIGenerationTask> getTasksByUser(String userId, Page<AIGenerationTask> page);
    
    /**
     * 获取任务列表（分页查询）
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 关键词
     * @param taskType 任务类型
     * @param status 状态
     * @param username 用户名
     * @return 任务VO列表
     */
    List<AIGenerationTaskVO> getTasks(Integer current, Integer size, String keyword, String taskType, String status, String username);
    
    /**
     * 获取任务详情
     * @param taskId 任务ID
     * @return 任务详情
     */
    AIGenerationTask getTaskById(Long taskId);
    
    /**
     * 获取任务详情（VO转换）
     * @param taskId 任务ID
     * @return 任务VO
     */
    AIGenerationTaskVO getTaskVO(Long taskId);
    
    /**
     * 删除任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean deleteTask(Long taskId);
    
    /**
     * 重试任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean retryTask(Long taskId);
} 