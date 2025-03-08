package com.echo.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echo.blog.entity.AIGenerationTask;
import com.echo.blog.mapper.AIGenerationTaskMapper;
import com.echo.blog.model.dto.AIGenerationTaskDTO;
import com.echo.blog.model.vo.AIGenerationTaskVO;
import com.echo.blog.service.AIGenerationTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI内容生成任务服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIGenerationTaskServiceImpl extends ServiceImpl<AIGenerationTaskMapper, AIGenerationTask> implements AIGenerationTaskService {

    private final AIGenerationTaskMapper aiGenerationTaskMapper;

    @Override
    @Transactional
    public AIGenerationTask createTask(AIGenerationTask task) {
        task.setStatus("PENDING")
            .setCreatedTime(LocalDateTime.now())
            .setUpdatedTime(LocalDateTime.now());
        save(task);
        return task;
    }
    
    @Override
    @Transactional
    public AIGenerationTaskVO createTask(AIGenerationTaskDTO taskDTO, String username) {
        AIGenerationTask task = new AIGenerationTask();
        BeanUtils.copyProperties(taskDTO, task);
        task.setCreatedBy(username);
        task = createTask(task);
        
        AIGenerationTaskVO taskVO = new AIGenerationTaskVO();
        BeanUtils.copyProperties(task, taskVO);
        return taskVO;
    }

    @Override
    @Transactional
    public AIGenerationTask updateTaskStatus(Long taskId, String status, String errorMessage) {
        AIGenerationTask task = getById(taskId);
        if (task == null) {
            return null;
        }
        
        task.setStatus(status)
            .setErrorMessage(errorMessage)
            .setUpdatedTime(LocalDateTime.now());
        updateById(task);
        return task;
    }

    @Override
    @Transactional
    public AIGenerationTask updateTaskResult(Long taskId, Long articleId) {
        AIGenerationTask task = getById(taskId);
        if (task == null) {
            return null;
        }
        
        task.setResultArticleId(articleId)
            .setStatus("COMPLETED")
            .setUpdatedTime(LocalDateTime.now());
        updateById(task);
        return task;
    }

    @Override
    public List<AIGenerationTask> getPendingTasks(int limit) {
        return aiGenerationTaskMapper.findPendingTasks(limit);
    }

    @Override
    public Page<AIGenerationTask> getTasksByUser(String userId, Page<AIGenerationTask> page) {
        LambdaQueryWrapper<AIGenerationTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AIGenerationTask::getCreatedBy, userId)
                   .orderByDesc(AIGenerationTask::getCreatedTime);
        return page(page, queryWrapper);
    }
    
    @Override
    public List<AIGenerationTaskVO> getTasks(Integer current, Integer size, String keyword, String taskType, String status, String username) {
        Page<AIGenerationTask> page = new Page<>(current, size);
        LambdaQueryWrapper<AIGenerationTask> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        queryWrapper.eq(AIGenerationTask::getCreatedBy, username);
        
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like(AIGenerationTask::getTaskName, keyword)
                       .or()
                       .like(AIGenerationTask::getDescription, keyword);
        }
        
        if (taskType != null && !taskType.isEmpty()) {
            queryWrapper.eq(AIGenerationTask::getTaskType, taskType);
        }
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(AIGenerationTask::getStatus, status);
        }
        
        queryWrapper.orderByDesc(AIGenerationTask::getCreatedTime);
        
        Page<AIGenerationTask> taskPage = page(page, queryWrapper);
        
        // 转换为VO
        return taskPage.getRecords().stream()
                .map(task -> {
                    AIGenerationTaskVO vo = new AIGenerationTaskVO();
                    BeanUtils.copyProperties(task, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public AIGenerationTask getTaskById(Long taskId) {
        return getById(taskId);
    }
    
    @Override
    public AIGenerationTaskVO getTaskVO(Long taskId) {
        AIGenerationTask task = getById(taskId);
        if (task == null) {
            return null;
        }
        
        AIGenerationTaskVO taskVO = new AIGenerationTaskVO();
        BeanUtils.copyProperties(task, taskVO);
        return taskVO;
    }

    @Override
    @Transactional
    public boolean deleteTask(Long taskId) {
        return removeById(taskId);
    }
    
    @Override
    @Transactional
    public boolean retryTask(Long taskId) {
        AIGenerationTask task = getById(taskId);
        if (task == null) {
            return false;
        }
        
        // 只有失败的任务才能重试
        if (!"FAILED".equals(task.getStatus())) {
            return false;
        }
        
        // 更新任务状态为待处理
        task.setStatus("PENDING")
            .setErrorMessage(null)
            .setUpdatedTime(LocalDateTime.now());
        return updateById(task);
    }
} 