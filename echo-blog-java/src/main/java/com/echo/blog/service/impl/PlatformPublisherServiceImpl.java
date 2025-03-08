package com.echo.blog.service.impl;

import com.echo.blog.entity.Article;
import com.echo.blog.entity.PlatformConfig;
import com.echo.blog.entity.UserPlatformAuth;
import com.echo.blog.enums.PlatformType;
import com.echo.blog.service.PlatformPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 平台发布服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformPublisherServiceImpl implements PlatformPublisherService {
    
    private final ApplicationContext applicationContext;
    
    @Override
    public String publishArticle(Article article, UserPlatformAuth auth, PlatformConfig platformConfig) throws Exception {
        try {
            String platformType = platformConfig.getPlatformType().toLowerCase();
            String beanName = platformType + "Publisher";
            
            if (applicationContext.containsBean(beanName)) {
                PlatformPublisherService publisher = applicationContext.getBean(beanName, PlatformPublisherService.class);
                return publisher.publishArticle(article, auth, platformConfig);
            } else {
                log.error("未找到平台 {} 的发布服务", platformType);
                throw new Exception("未找到平台的发布服务");
            }
        } catch (Exception e) {
            log.error("发布文章到平台失败", e);
            throw e;
        }
    }
    
    /**
     * 兼容旧代码的方法
     */
    public boolean publishArticle(Article article, PlatformConfig platformConfig) {
        try {
            // 这里简单实现，实际应该从数据库获取auth信息
            UserPlatformAuth auth = new UserPlatformAuth();
            auth.setUserId(Long.valueOf(article.getCreatedBy()));
            auth.setPlatformType(PlatformType.valueOf(platformConfig.getPlatformType()));
            
            String result = publishArticle(article, auth, platformConfig);
            return result != null && !result.isEmpty();
        } catch (Exception e) {
            log.error("发布文章到平台失败", e);
            return false;
        }
    }
}