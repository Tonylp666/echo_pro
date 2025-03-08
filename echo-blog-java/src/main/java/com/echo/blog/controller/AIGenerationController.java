package com.echo.blog.controller;

import com.echo.blog.common.api.ApiResult;
import com.echo.blog.entity.AIGenerationTask;
import com.echo.blog.model.dto.AIGenerationTaskDTO;
import com.echo.blog.model.dto.ContentGenerationDTO;
import com.echo.blog.model.vo.AIGenerationTaskVO;
import com.echo.blog.service.AIContentGenerationService;
import com.echo.blog.service.AIGenerationTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * AI 内容生成控制器
 */
@RestController
@RequestMapping("/api/ai/generation")
@RequiredArgsConstructor
@Tag(name = "AI内容生成", description = "AI内容生成相关接口")
public class AIGenerationController {
    
    private final AIContentGenerationService aiContentGenerationService;
    private final AIGenerationTaskService aiGenerationTaskService;
    
    /**
     * 创建生成任务
     *
     * @param taskDTO 任务信息
     * @param authentication 认证信息
     * @return 任务信息
     */
    @PostMapping("/task")
    @Operation(summary = "创建生成任务")
    public ApiResult<AIGenerationTaskVO> createTask(
            @RequestBody @Valid AIGenerationTaskDTO taskDTO,
            Authentication authentication) {
        String username = authentication.getName();
        AIGenerationTaskVO taskVO = aiGenerationTaskService.createTask(taskDTO, username);
        return ApiResult.success(taskVO);
    }
    
    /**
     * 获取任务列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 关键词
     * @param taskType 任务类型
     * @param status 状态
     * @param authentication 认证信息
     * @return 任务列表
     */
    @GetMapping("/tasks")
    @Operation(summary = "获取任务列表")
    public ApiResult<List<AIGenerationTaskVO>> getTasks(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        String username = authentication.getName();
        List<AIGenerationTaskVO> tasks = aiGenerationTaskService.getTasks(
                current, size, keyword, taskType, status, username);
        return ApiResult.success(tasks);
    }
    
    /**
     * 获取任务详情
     *
     * @param taskId 任务ID
     * @return 任务详情
     */
    @GetMapping("/task/{taskId}")
    @Operation(summary = "获取任务详情")
    public ApiResult<AIGenerationTaskVO> getTaskDetail(@PathVariable Long taskId) {
        AIGenerationTaskVO taskVO = aiGenerationTaskService.getTaskVO(taskId);
        return ApiResult.success(taskVO);
    }
    
    /**
     * 重试任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    @PostMapping("/task/{taskId}/retry")
    @Operation(summary = "重试任务")
    public ApiResult<Boolean> retryTask(@PathVariable Long taskId) {
        boolean result = aiGenerationTaskService.retryTask(taskId);
        return ApiResult.success(result);
    }
    
    /**
     * 删除任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    @DeleteMapping("/task/{taskId}")
    @Operation(summary = "删除任务")
    public ApiResult<Boolean> deleteTask(@PathVariable Long taskId) {
        boolean result = aiGenerationTaskService.deleteTask(taskId);
        return ApiResult.success(result);
    }
    
    /**
     * 生成标题
     *
     * @param contentDTO 内容信息
     * @return 生成的标题
     */
    @PostMapping("/title")
    @Operation(summary = "生成标题")
    public ApiResult<String> generateTitle(@RequestBody ContentGenerationDTO contentDTO) {
        String title = aiContentGenerationService.generateTitle(contentDTO.getContent());
        return ApiResult.success(title);
    }
    
    /**
     * 生成摘要
     *
     * @param contentDTO 内容信息
     * @return 生成的摘要
     */
    @PostMapping("/summary")
    @Operation(summary = "生成摘要")
    public ApiResult<String> generateSummary(@RequestBody ContentGenerationDTO contentDTO) {
        String summary = aiContentGenerationService.generateSummary(contentDTO.getContent());
        return ApiResult.success(summary);
    }
    
    /**
     * 优化内容
     *
     * @param contentDTO 内容信息
     * @return 优化后的内容
     */
    @PostMapping("/improve")
    @Operation(summary = "优化内容")
    public ApiResult<String> improveContent(@RequestBody ContentGenerationDTO contentDTO) {
        String improvedContent = aiContentGenerationService.improveContent(contentDTO.getContent());
        return ApiResult.success(improvedContent);
    }
} 