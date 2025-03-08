package com.echo.blog.security;

import com.echo.blog.service.ArticleService;
import com.echo.blog.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 权限检查切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionCheckAspect {
    
    private final ArticleService articleService;
    private final TaskService taskService;
    
    /**
     * 权限检查
     *
     * @param joinPoint 连接点
     * @param requirePermission 权限注解
     */
    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new SecurityException("未授权的访问");
        }
        
        String requiredPermission = requirePermission.value();
        
        // 检查用户是否有所需权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasPermission = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(requiredPermission));
        
        if (!hasPermission) {
            throw new SecurityException("没有足够的权限执行此操作");
        }
        
        // 检查资源所有权（如果需要）
        if (requiredPermission.startsWith("article:") && !requiredPermission.equals("article:create")) {
            checkArticleOwnership(joinPoint, authentication);
        } else if (requiredPermission.startsWith("task:") && !requiredPermission.equals("task:create")) {
            checkTaskOwnership(joinPoint, authentication);
        }
    }
    
    /**
     * 检查文章所有权
     *
     * @param joinPoint 连接点
     * @param authentication 认证信息
     */
    private void checkArticleOwnership(JoinPoint joinPoint, Authentication authentication) {
        Long articleId = extractResourceId(joinPoint, "articleId");
        if (articleId != null) {
            String userId = authentication.getName();
            boolean hasPermission = articleService.checkArticlePermission(articleId, userId);
            if (!hasPermission) {
                throw new SecurityException("没有权限操作此文章");
            }
        }
    }
    
    /**
     * 检查任务所有权
     *
     * @param joinPoint 连接点
     * @param authentication 认证信息
     */
    private void checkTaskOwnership(JoinPoint joinPoint, Authentication authentication) {
        Long taskId = extractResourceId(joinPoint, "taskId");
        if (taskId != null) {
            String userId = authentication.getName();
            boolean hasPermission = taskService.checkTaskPermission(taskId, userId);
            if (!hasPermission) {
                throw new SecurityException("没有权限操作此任务");
            }
        }
    }
    
    /**
     * 提取资源ID
     *
     * @param joinPoint 连接点
     * @param paramName 参数名
     * @return 资源ID
     */
    private Long extractResourceId(JoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(paramName) && args[i] instanceof Long) {
                return (Long) args[i];
            }
        }
        
        return null;
    }
}