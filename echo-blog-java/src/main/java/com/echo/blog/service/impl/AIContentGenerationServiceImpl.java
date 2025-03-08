package com.echo.blog.service.impl;

import com.echo.blog.config.AIModelProperties;
import com.echo.blog.entity.AIGenerationTask;
import com.echo.blog.entity.Article;
import com.echo.blog.service.AIContentGenerationService;
import com.echo.blog.service.AIGenerationTaskService;
import com.echo.blog.service.ArticleService;
import com.echo.blog.service.CrawlerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * AI内容生成服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIContentGenerationServiceImpl implements AIContentGenerationService {

    private final AIGenerationTaskService aiGenerationTaskService;
    private final ArticleService articleService;
    private final CrawlerService crawlerService;
    private final AIModelProperties aiModelProperties;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Article generateFromCrawler(AIGenerationTask task) {
        try {
            // 更新任务状态为处理中
            aiGenerationTaskService.updateTaskStatus(task.getId(), "PROCESSING", null);
            
            // 解析源数据
            String sourceData = task.getSourceData();
            log.info("开始根据爬虫数据生成文章，任务ID：{}，源数据：{}", task.getId(), sourceData);
            
            // 调用AI模型生成文章内容
            String prompt = task.getPrompt();
            String aiModel = task.getAiModel();
            String content = generateContent(sourceData, prompt, aiModel);
            
            // 创建文章
            Article article = new Article();
            article.setTitle(task.getTaskName())
                  .setContent(content)
                  .setContentMarkdown(content)
                  .setStatus("DRAFT")
                  .setCreatedBy(task.getCreatedBy())
                  .setCreatedTime(LocalDateTime.now())
                  .setUpdatedTime(LocalDateTime.now());
            
            articleService.save(article);
            
            // 更新任务状态为已完成
            aiGenerationTaskService.updateTaskResult(task.getId(), article.getId());
            
            return article;
        } catch (Exception e) {
            log.error("根据爬虫数据生成文章失败，任务ID：{}，错误：{}", task.getId(), e.getMessage(), e);
            aiGenerationTaskService.updateTaskStatus(task.getId(), "FAILED", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public Article generateFromSearch(AIGenerationTask task) {
        try {
            // 更新任务状态为处理中
            aiGenerationTaskService.updateTaskStatus(task.getId(), "PROCESSING", null);
            
            // 解析源数据
            String sourceData = task.getSourceData();
            log.info("开始根据搜索数据生成文章，任务ID：{}，源数据：{}", task.getId(), sourceData);
            
            // 调用AI模型生成文章内容
            String prompt = task.getPrompt();
            String aiModel = task.getAiModel();
            String content = generateContent(sourceData, prompt, aiModel);
            
            // 创建文章
            Article article = new Article();
            article.setTitle(task.getTaskName())
                  .setContent(content)
                  .setContentMarkdown(content)
                  .setStatus("DRAFT")
                  .setCreatedBy(task.getCreatedBy())
                  .setCreatedTime(LocalDateTime.now())
                  .setUpdatedTime(LocalDateTime.now());
            
            articleService.save(article);
            
            // 更新任务状态为已完成
            aiGenerationTaskService.updateTaskResult(task.getId(), article.getId());
            
            return article;
        } catch (Exception e) {
            log.error("根据搜索数据生成文章失败，任务ID：{}，错误：{}", task.getId(), e.getMessage(), e);
            aiGenerationTaskService.updateTaskStatus(task.getId(), "FAILED", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public Article generateFromHotspot(AIGenerationTask task) {
        try {
            // 更新任务状态为处理中
            aiGenerationTaskService.updateTaskStatus(task.getId(), "PROCESSING", null);
            
            // 解析源数据
            String sourceData = task.getSourceData();
            log.info("开始根据热点数据生成文章，任务ID：{}，源数据：{}", task.getId(), sourceData);
            
            // 调用AI模型生成文章内容
            String prompt = task.getPrompt();
            String aiModel = task.getAiModel();
            String content = generateContent(sourceData, prompt, aiModel);
            
            // 创建文章
            Article article = new Article();
            article.setTitle(task.getTaskName())
                  .setContent(content)
                  .setContentMarkdown(content)
                  .setStatus("DRAFT")
                  .setCreatedBy(task.getCreatedBy())
                  .setCreatedTime(LocalDateTime.now())
                  .setUpdatedTime(LocalDateTime.now());
            
            articleService.save(article);
            
            // 更新任务状态为已完成
            aiGenerationTaskService.updateTaskResult(task.getId(), article.getId());
            
            return article;
        } catch (Exception e) {
            log.error("根据热点数据生成文章失败，任务ID：{}，错误：{}", task.getId(), e.getMessage(), e);
            aiGenerationTaskService.updateTaskStatus(task.getId(), "FAILED", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean processTask(Long taskId) {
        AIGenerationTask task = aiGenerationTaskService.getTaskById(taskId);
        if (task == null) {
            log.error("任务不存在，任务ID：{}", taskId);
            return false;
        }
        
        if (!"PENDING".equals(task.getStatus())) {
            log.warn("任务状态不是待处理，无法处理，任务ID：{}，当前状态：{}", taskId, task.getStatus());
            return false;
        }
        
        try {
            switch (task.getTaskType()) {
                case "CRAWLER":
                    generateFromCrawler(task);
                    break;
                case "SEARCH":
                    generateFromSearch(task);
                    break;
                case "HOTSPOT":
                    generateFromHotspot(task);
                    break;
                default:
                    log.error("不支持的任务类型，任务ID：{}，类型：{}", taskId, task.getTaskType());
                    aiGenerationTaskService.updateTaskStatus(taskId, "FAILED", "不支持的任务类型：" + task.getTaskType());
                    return false;
            }
            return true;
        } catch (Exception e) {
            log.error("处理任务失败，任务ID：{}，错误：{}", taskId, e.getMessage(), e);
            aiGenerationTaskService.updateTaskStatus(taskId, "FAILED", e.getMessage());
            return false;
        }
    }
    
    /**
     * 调用AI模型生成内容
     */
    private String generateContent(String sourceData, String prompt, String aiModel) {
        // TODO: 实现AI模型调用
        // 这里是示例实现，实际项目中需要根据选择的AI模型进行调用
        if ("deepseek".equals(aiModel)) {
            return generateWithDeepseek(sourceData, prompt);
        } else if ("aliyun".equals(aiModel)) {
            return generateWithAliyun(sourceData, prompt);
        } else {
            throw new IllegalArgumentException("不支持的AI模型：" + aiModel);
        }
    }
    
    /**
     * 使用DeepSeek生成内容
     */
    private String generateWithDeepseek(String sourceData, String prompt) {
        // TODO: 实现DeepSeek API调用
        // 这里是示例实现，实际项目中需要调用DeepSeek API
        String fullPrompt = String.format("根据以下数据生成一篇文章：\n\n数据：%s\n\n要求：%s", sourceData, prompt);
        log.info("调用DeepSeek API，提示词：{}", fullPrompt);
        
        // 模拟API调用
        return "这是由DeepSeek生成的文章内容，基于提供的数据和要求。\n\n"
             + "# 文章标题\n\n"
             + "## 第一部分\n\n这是第一部分的内容...\n\n"
             + "## 第二部分\n\n这是第二部分的内容...\n\n"
             + "## 总结\n\n这是总结部分...";
    }
    
    /**
     * 使用阿里云百炼生成内容
     */
    private String generateWithAliyun(String sourceData, String prompt) {
        // TODO: 实现阿里云百炼SDK调用
        // 这里是示例实现，实际项目中需要调用阿里云百炼SDK
        String fullPrompt = String.format("根据以下数据生成一篇文章：\n\n数据：%s\n\n要求：%s", sourceData, prompt);
        log.info("调用阿里云百炼SDK，提示词：{}", fullPrompt);
        
        // 模拟SDK调用
        return "这是由阿里云百炼生成的文章内容，基于提供的数据和要求。\n\n"
             + "# 文章标题\n\n"
             + "## 第一部分\n\n这是第一部分的内容...\n\n"
             + "## 第二部分\n\n这是第二部分的内容...\n\n"
             + "## 总结\n\n这是总结部分...";
    }
    
    @Override
    public String generateTitle(String content) {
        // 调用AI模型生成标题
        log.info("开始生成标题，内容长度：{}", content.length());
        
        // 截取内容前500个字符作为输入
        String inputContent = content.length() > 500 ? content.substring(0, 500) : content;
        
        // 构建提示词
        String prompt = "请为以下内容生成一个吸引人的标题：\n\n" + inputContent;
        
        // 默认使用阿里云百炼模型
        try {
            // 模拟API调用
            log.info("调用AI模型生成标题，提示词长度：{}", prompt.length());
            Thread.sleep(500); // 模拟API调用延迟
            
            return "基于内容生成的吸引人的标题";
        } catch (Exception e) {
            log.error("生成标题失败，错误：{}", e.getMessage(), e);
            return "生成标题失败";
        }
    }
    
    @Override
    public String generateSummary(String content) {
        // 调用AI模型生成摘要
        log.info("开始生成摘要，内容长度：{}", content.length());
        
        // 截取内容前1000个字符作为输入
        String inputContent = content.length() > 1000 ? content.substring(0, 1000) : content;
        
        // 构建提示词
        String prompt = "请为以下内容生成一个简洁的摘要（不超过200字）：\n\n" + inputContent;
        
        // 默认使用阿里云百炼模型
        try {
            // 模拟API调用
            log.info("调用AI模型生成摘要，提示词长度：{}", prompt.length());
            Thread.sleep(800); // 模拟API调用延迟
            
            return "这是一个由AI生成的摘要，简要概括了文章的主要内容和观点。摘要应该简洁明了，让读者能够快速了解文章的核心内容，同时又能吸引读者阅读全文。";
        } catch (Exception e) {
            log.error("生成摘要失败，错误：{}", e.getMessage(), e);
            return "生成摘要失败";
        }
    }
    
    @Override
    public String improveContent(String content) {
        // 调用AI模型优化内容
        log.info("开始优化内容，内容长度：{}", content.length());
        
        // 构建提示词
        String prompt = "请优化以下内容，使其更加流畅、专业，并修正可能存在的语法错误：\n\n" + content;
        
        // 默认使用阿里云百炼模型
        try {
            // 模拟API调用
            log.info("调用AI模型优化内容，提示词长度：{}", prompt.length());
            Thread.sleep(1500); // 模拟API调用延迟
            
            // 简单模拟优化结果
            String improved = content.replace("。", "。\n\n")
                                    .replace("！", "！\n\n")
                                    .replace("？", "？\n\n");
            
            return "# " + generateTitle(content) + "\n\n" + improved;
        } catch (Exception e) {
            log.error("优化内容失败，错误：{}", e.getMessage(), e);
            return content;
        }
    }
} 