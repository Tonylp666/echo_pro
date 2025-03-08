package com.echo.blog.service.publish;

import com.echo.blog.entity.Article;
import com.echo.blog.entity.PlatformConfig;
import com.echo.blog.entity.UserPlatformAuth;
import com.echo.blog.service.ArticleSubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 知乎发布服务
 */
@Slf4j
@Service("zhihuPublisher")
public class ZhihuPublisher extends AbstractPlatformPublisher {

    public ZhihuPublisher(ArticleSubService articleSubService) {
        super(articleSubService);
    }

    @Override
    protected String getPlatformType() {
        return "ZHIHU";
    }

    @Override
    protected String convertContent(String markdown) {
        // 知乎支持Markdown，但可能需要一些调整
        log.info("转换Markdown为知乎格式");
        return markdown;
    }

    @Override
    protected String doPublish(Article article, String platformContent, UserPlatformAuth auth, PlatformConfig platformConfig) throws Exception {
        // 实现知乎发布
        log.info("发布文章到知乎，文章ID：{}，标题：{}", article.getId(), article.getTitle());
        
        // 使用auth中的授权信息
        log.info("使用用户授权信息：用户ID={}, 平台类型={}", auth.getUserId(), auth.getPlatformType());
        
        // 模拟发布过程
        Thread.sleep(1000);
        
        // 返回平台元数据
        return "zhihu_" + article.getId();
    }
} 