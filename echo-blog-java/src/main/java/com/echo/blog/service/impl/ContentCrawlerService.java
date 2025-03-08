package com.echo.blog.service.impl;

import com.echo.blog.entity.Article;
import com.echo.blog.entity.CrawlerSource;
import com.echo.blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContentCrawlerService {

    private final ArticleService articleService;
    private final RestTemplate restTemplate;

    @Value("${bing.api.key}")
    private String bingApiKey;

    @Value("${bing.api.endpoint}")
    private String bingApiEndpoint;

    public List<Article> crawlContent(CrawlerSource source) {
        List<Article> articles = new ArrayList<>();

        try {
            switch (source.getSourceChannel().toUpperCase()) {
                case "ZHIHU":
                    articles.addAll(crawlZhihu(source));
                    break;
                case "WEIBO":
                    articles.addAll(crawlWeibo(source));
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported source channel: " + source.getSourceChannel());
            }

            // 使用Bing Search API补充信息
            enrichArticlesWithBingSearch(articles);

            // 保存文章
            articles.forEach(articleService::createArticle);

            return articles;
        } catch (Exception e) {
            throw new RuntimeException("Failed to crawl content: " + e.getMessage(), e);
        }
    }

    private List<Article> crawlZhihu(CrawlerSource source) throws Exception {
        List<Article> articles = new ArrayList<>();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", source.getCookie());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Document doc = Jsoup.connect(source.getUrl())
                .headers(headers.toSingleValueMap())
                .get();

        doc.select(".ContentItem").stream()
                .limit(source.getTopK())
                .forEach(element -> {
                    Article article = new Article()
                            .setTitle(element.select(".ContentItem-title").text())
                            .setContent(element.select(".RichContent-inner").html())
                            .setStatus("DRAFT")
                            .setCreatedBy(source.getCreatedBy())
                            .setCreatedTime(LocalDateTime.now())
                            .setUpdatedTime(LocalDateTime.now())
                            .setViewCount(0L)
                            .setLikeCount(0L);
                    articles.add(article);
                });

        return articles;
    }

    private List<Article> crawlWeibo(CrawlerSource source) throws Exception {
        List<Article> articles = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", source.getCookie());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Document doc = Jsoup.connect(source.getUrl())
                .headers(headers.toSingleValueMap())
                .get();

        doc.select(".weibo-detail").stream()
                .limit(source.getTopK())
                .forEach(element -> {
                    Article article = new Article()
                            .setTitle(element.select(".weibo-text").text())
                            .setContent(element.select(".weibo-content").html())
                            .setStatus("DRAFT")
                            .setCreatedBy(source.getCreatedBy())
                            .setCreatedTime(LocalDateTime.now())
                            .setUpdatedTime(LocalDateTime.now())
                            .setViewCount(0L)
                            .setLikeCount(0L);
                    articles.add(article);
                });

        return articles;
    }

    private void enrichArticlesWithBingSearch(List<Article> articles) {
        articles.forEach(article -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Ocp-Apim-Subscription-Key", bingApiKey);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.getForEntity(
                    bingApiEndpoint + "?q=" + article.getTitle() + "&count=5",
                    Map.class,
                    entity
                );

                if (response.getBody() != null && response.getBody().containsKey("webPages")) {
                    Map<String, Object> webPages = (Map<String, Object>) response.getBody().get("webPages");
                    List<Map<String, Object>> values = (List<Map<String, Object>>) webPages.get("value");

                    StringBuilder enrichedContent = new StringBuilder(article.getContent());
                    enrichedContent.append("\n\n相关资料：\n");
                    
                    values.forEach(value -> {
                        enrichedContent.append("- ").append(value.get("name"))
                                .append(": ").append(value.get("snippet"))
                                .append("\n");
                    });

                    article.setContent(enrichedContent.toString());
                }
            } catch (Exception e) {
                // 如果补充信息失败，继续处理下一篇文章
                System.err.println("Failed to enrich article: " + article.getTitle());
            }
        });
    }
}