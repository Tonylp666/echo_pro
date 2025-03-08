package com.echo.blog.service.publish;

import com.echo.blog.entity.Article;
import com.echo.blog.entity.PlatformConfig;
import com.echo.blog.entity.UserPlatformAuth;
import com.echo.blog.service.ArticleSubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信公众号发布服务
 */
@Slf4j
@Service("weixinPublisher")
public class WeixinPublisher extends AbstractPlatformPublisher {
    
    public WeixinPublisher(ArticleSubService articleSubService) {
        super(articleSubService);
    }
    
    @Override
    protected String getPlatformType() {
        return "WEIXIN";
    }
    
    @Override
    protected String convertContent(String markdown) {
        // 实现Markdown转微信公众号格式
        log.info("转换Markdown为微信公众号格式");
        
        // 简单替换一些Markdown语法
        String html = markdown;
        
        // 标题转换
        html = html.replaceAll("# (.*)", "<h1>$1</h1>");
        html = html.replaceAll("## (.*)", "<h2>$1</h2>");
        html = html.replaceAll("### (.*)", "<h3>$1</h3>");
        
        // 加粗和斜体
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        html = html.replaceAll("\\*(.*?)\\*", "<em>$1</em>");
        
        // 链接
        html = html.replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\"$2\">$1</a>");
        
        // 图片
        html = html.replaceAll("!\\[(.*?)\\]\\((.*?)\\)", "<img src=\"$2\" alt=\"$1\">");
        
        return html;
    }
    
    @Override
    protected String doPublish(Article article, String platformContent, UserPlatformAuth auth, PlatformConfig platformConfig) throws Exception {
        // 实现微信公众号发布
        log.info("发布文章到微信公众号，文章ID：{}，标题：{}", article.getId(), article.getTitle());
        
        // 使用auth中的授权信息
        log.info("使用用户授权信息：用户ID={}, 平台类型={}", auth.getUserId(), auth.getPlatformType());
        
        // 模拟发布过程
        Thread.sleep(1000);
        
        // 返回平台元数据
        return "weixin_" + article.getId();
    }
}