package com.echo.blog.service.impl;

import com.echo.blog.entity.Article;
import com.echo.blog.entity.ArticleSub;
import com.echo.blog.entity.PlatformConfig;
import com.echo.blog.entity.UserPlatformAuth;
import com.echo.blog.enums.PlatformType;
import com.echo.blog.model.dto.ArticlePublishDTO;
import com.echo.blog.model.vo.ArticlePublishResultVO;
import com.echo.blog.repository.ArticleRepository;
import com.echo.blog.repository.ArticleSubRepository;
import com.echo.blog.repository.PlatformConfigRepository;
import com.echo.blog.service.ArticlePublishService;
import com.echo.blog.service.ArticleService;
import com.echo.blog.service.ArticleSubService;
import com.echo.blog.service.PlatformConfigService;
import com.echo.blog.service.PlatformPublisherService;
import com.echo.blog.service.UserPlatformAuthService;
import com.echo.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章发布服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticlePublishServiceImpl implements ArticlePublishService {

    private final ArticleService articleService;
    private final ArticleSubService articleSubService;
    private final PlatformConfigService platformConfigService;
    private final ApplicationContext applicationContext;
    private final ArticleRepository articleRepository;
    private final ArticleSubRepository articleSubRepository;
    private final PlatformConfigRepository platformConfigRepository;
    private final UserService userService;
    private final UserPlatformAuthService userPlatformAuthService;
    private final Map<String, PlatformPublisherService> publisherServices;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ArticlePublishResultVO publishArticle(ArticlePublishDTO publishDTO, String username) {
        ArticlePublishResultVO result = new ArticlePublishResultVO();
        result.setArticleId(publishDTO.getArticleId());
        result.setPlatformType(publishDTO.getPlatformType());
        
        try {
            // 1. 获取文章
            Article article = articleRepository.findById(publishDTO.getArticleId())
                    .orElseThrow(() -> new RuntimeException("文章不存在"));
            
            // 2. 检查用户是否有权限发布文章
            if (!article.getCreatedBy().equals(username)) {
                throw new RuntimeException("无权发布该文章");
            }
            
            // 3. 检查用户是否授权该平台
            Long userId = Long.valueOf(username);
            UserPlatformAuth auth = userPlatformAuthService.getUserPlatformAuth(userId, publishDTO.getPlatformType());
            if (auth == null) {
                throw new RuntimeException("未授权该平台");
            }
            
            // 4. 获取平台配置
            PlatformConfig platformConfig = platformConfigRepository.findByPlatformType(publishDTO.getPlatformType().toString());
            if (platformConfig == null) {
                throw new RuntimeException("平台配置不存在");
            }
            
            // 5. 合并用户配置
            if (publishDTO.getConfig() != null) {
                Map<String, Object> mergedConfig = new HashMap<>();
                if (platformConfig.getExtraConfig() != null) {
                    mergedConfig.putAll(objectMapper.readValue(platformConfig.getExtraConfig(), Map.class));
                }
                mergedConfig.putAll(publishDTO.getConfig());
                platformConfig.setExtraConfig(objectMapper.writeValueAsString(mergedConfig));
            }
            
            // 6. 获取对应的发布服务
            PlatformPublisherService publisherService = publisherServices.get(publishDTO.getPlatformType().toString());
            if (publisherService == null) {
                throw new RuntimeException("不支持该平台");
            }
            
            // 7. 发布文章
            String platformArticleId = publisherService.publishArticle(article, auth, platformConfig);
            
            // 8. 保存发布记录
            ArticleSub articleSub = new ArticleSub();
            articleSub.setArticleId(article.getId());
            articleSub.setSubType(publishDTO.getPlatformType().name());
            articleSub.setSubId(platformArticleId);
            articleSub.setStatus("SUCCESS");
            articleSub.setCreatedTime(LocalDateTime.now());
            articleSub.setUpdatedTime(LocalDateTime.now());
            articleSubRepository.save(articleSub);
            
            // 9. 设置结果
            result.setPlatformArticleId(platformArticleId);
            result.setSuccess(true);
            
            return result;
        } catch (Exception e) {
            log.error("发布文章失败", e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            return result;
        }
    }

    @Override
    public Map<String, Boolean> publishArticle(Long articleId, List<String> platformTypes) {
        Article article = articleService.getById(articleId);
        if (article == null) {
            log.error("文章不存在，文章ID：{}", articleId);
            return new HashMap<>();
        }
        
        // 获取用户ID
        String createdBy = article.getCreatedBy();
        Long userId = Long.parseLong(createdBy);
        
        Map<String, Boolean> results = new HashMap<>();
        
        for (String platformType : platformTypes) {
            try {
                // 获取平台配置
                PlatformConfig platformConfig = platformConfigService.getConfigByUserAndPlatform(userId, platformType);
                if (platformConfig == null) {
                    log.warn("平台配置不存在，用户ID：{}，平台：{}", userId, platformType);
                    results.put(platformType, false);
                    continue;
                }
                
                // 获取用户授权信息
                UserPlatformAuth auth = userPlatformAuthService.getUserPlatformAuth(userId, platformType);
                if (auth == null) {
                    log.warn("用户未授权该平台，用户ID：{}，平台：{}", userId, platformType);
                    results.put(platformType, false);
                    continue;
                }
                
                // 获取平台发布服务
                PlatformPublisherService publisherService = getPublisherService(platformType);
                if (publisherService == null) {
                    log.warn("平台发布服务不存在，平台：{}", platformType);
                    results.put(platformType, false);
                    continue;
                }
                
                // 发布文章
                String platformArticleId = publisherService.publishArticle(article, auth, platformConfig);
                results.put(platformType, platformArticleId != null && !platformArticleId.isEmpty());
            } catch (Exception e) {
                log.error("发布文章失败，文章ID：{}，平台：{}，错误：{}", articleId, platformType, e.getMessage(), e);
                results.put(platformType, false);
            }
        }
        
        return results;
    }

    @Override
    public Map<String, Boolean> publishArticleToAllPlatforms(Long articleId) {
        Article article = articleService.getById(articleId);
        if (article == null) {
            log.error("文章不存在，文章ID：{}", articleId);
            return new HashMap<>();
        }
        
        // 获取用户ID
        String createdBy = article.getCreatedBy();
        Long userId = Long.parseLong(createdBy);
        
        // 获取用户的所有平台配置
        List<PlatformConfig> platformConfigs = platformConfigService.getEnabledConfigsByUser(userId);
        
        Map<String, Boolean> results = new HashMap<>();
        
        for (PlatformConfig platformConfig : platformConfigs) {
            try {
                String platformType = platformConfig.getPlatformType();
                
                // 获取用户授权信息
                UserPlatformAuth auth = userPlatformAuthService.getUserPlatformAuth(userId, platformType);
                if (auth == null) {
                    log.warn("用户未授权该平台，用户ID：{}，平台：{}", userId, platformType);
                    results.put(platformType, false);
                    continue;
                }
                
                // 获取平台发布服务
                PlatformPublisherService publisherService = getPublisherService(platformType);
                if (publisherService == null) {
                    log.warn("平台发布服务不存在，平台：{}", platformType);
                    results.put(platformType, false);
                    continue;
                }
                
                // 发布文章
                String platformArticleId = publisherService.publishArticle(article, auth, platformConfig);
                results.put(platformType, platformArticleId != null && !platformArticleId.isEmpty());
            } catch (Exception e) {
                log.error("发布文章失败，文章ID：{}，平台：{}，错误：{}", articleId, platformConfig.getPlatformType(), e.getMessage(), e);
                results.put(platformConfig.getPlatformType(), false);
            }
        }
        
        return results;
    }

    @Override
    public Map<String, String> getArticlePublishStatus(Long articleId) {
        List<ArticleSub> articleSubs = articleSubService.getByArticleId(articleId);
        
        return articleSubs.stream()
                .collect(Collectors.toMap(
                        ArticleSub::getSubType,
                        ArticleSub::getSyncStatus,
                        (v1, v2) -> v1
                ));
    }
    
    /**
     * 获取平台发布服务
     */
    private PlatformPublisherService getPublisherService(String platformType) {
        String beanName = platformType.toLowerCase() + "Publisher";
        if (applicationContext.containsBean(beanName)) {
            return applicationContext.getBean(beanName, PlatformPublisherService.class);
        }
        return null;
    }
} 