package com.echo.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echo.blog.common.api.ApiResult;
import com.echo.blog.entity.AIGenerationTask;
import com.echo.blog.model.dto.AIGenerationTaskDTO;
import com.echo.blog.model.vo.AIGenerationTaskVO;
import com.echo.blog.security.RequirePermission;
import com.echo.blog.service.AIGenerationTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI内容生成任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai-tasks")
@RequiredArgsConstructor
@Tag(name = "AI内容生成任务", description = "AI内容生成任务相关接口")
public class AIGenerationTaskController {

    private final AIGenerationTaskService aiGenerationTaskService;

    @PostMapping
    @Operation(summary = "创建AI生成任务")
    @RequirePermission("ai:task:create")
    public ApiResult<AIGenerationTaskVO> createTask(@RequestBody AIGenerationTaskDTO taskDTO, Authentication authentication) {
        AIGenerationTask task = new AIGenerationTask();
        BeanUtils.copyProperties(taskDTO, task);
        task.setCreatedBy(authentication.getName());
        
        AIGenerationTask createdTask = aiGenerationTaskService.createTask(task);
        AIGenerationTaskVO taskVO = convertToVO(createdTask);
        
        return ApiResult.success(taskVO);
    }

    @GetMapping
    @Operation(summary = "获取当前用户的任务列表")
    @RequirePermission("ai:task:list")
    public ApiResult<Page<AIGenerationTaskVO>> getTaskList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        
        Page<AIGenerationTask> page = new Page<>(current, size);
        Page<AIGenerationTask> taskPage = aiGenerationTaskService.getTasksByUser(authentication.getName(), page);
        
        List<AIGenerationTaskVO> taskVOList = taskPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        Page<AIGenerationTaskVO> resultPage = new Page<>(taskPage.getCurrent(), taskPage.getSize(), taskPage.getTotal());
        resultPage.setRecords(taskVOList);
        
        return ApiResult.success(resultPage);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "获取任务详情")
    @RequirePermission("ai:task:view")
    public ApiResult<AIGenerationTaskVO> getTaskDetail(@PathVariable Long taskId) {
        AIGenerationTask task = aiGenerationTaskService.getTaskById(taskId);
        if (task == null) {
            return ApiResult.failed("任务不存在");
        }
        
        AIGenerationTaskVO taskVO = convertToVO(task);
        return ApiResult.success(taskVO);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "更新任务状态")
    @RequirePermission("ai:task:update")
    public ApiResult<AIGenerationTaskVO> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam String status,
            @RequestParam(required = false) String errorMessage) {
        
        AIGenerationTask updatedTask = aiGenerationTaskService.updateTaskStatus(taskId, status, errorMessage);
        if (updatedTask == null) {
            return ApiResult.failed("任务不存在");
        }
        
        AIGenerationTaskVO taskVO = convertToVO(updatedTask);
        return ApiResult.success(taskVO);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "删除任务")
    @RequirePermission("ai:task:delete")
    public ApiResult<Boolean> deleteTask(@PathVariable Long taskId) {
        boolean result = aiGenerationTaskService.deleteTask(taskId);
        return result ? ApiResult.success(true) : ApiResult.failed("删除失败");
    }

    /**
     * 将实体转换为VO
     */
    private AIGenerationTaskVO convertToVO(AIGenerationTask task) {
        AIGenerationTaskVO vo = new AIGenerationTaskVO();
        BeanUtils.copyProperties(task, vo);
        return vo;
    }
} 