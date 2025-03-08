package com.echo.blog.service.impl;

import com.echo.blog.entity.Article;
import com.echo.blog.entity.ArticleTask;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotTopicCrawlerService {

    private final RestTemplate restTemplate;
    private final AIArticleGenerator aiArticleGenerator;
    private final ObjectMapper objectMapper;

    private static final String ZHIHU_API = "https://www.zhihu.com/api/v3/feed/topstory/hot-lists/total?limit=50&desktop=true";
    private static final String TOUTIAO_API = "https://www.toutiao.com/hot-event/hot-board/?origin=toutiao_pc";
    private static final String WEIBO_API = "https://weibo.com/ajax/side/hotSearch";
    private static final String BAIDU_API = "https://top.baidu.com/board?tab=realtime";

    @Scheduled(cron = "0 0 * * * *") // 每小时执行一次
    public void crawlAndGenerateArticle() {
        log.info("开始执行热点话题爬取任务");
        try {
            // 1. 获取所有热榜数据
            List<HotTopic> allTopics = new ArrayList<>();
            try {
                allTopics.addAll(crawlZhihuHotTopics());
                allTopics.addAll(crawlToutiaoHotTopics());
                allTopics.addAll(crawlWeiboHotTopics());
                allTopics.addAll(crawlBaiduHotTopics());
                log.info("成功获取{}条热点话题数据", allTopics.size());
            } catch (Exception e) {
                log.error("爬取热点话题数据失败: {}", e.getMessage());
                return;
            }

            if (allTopics.isEmpty()) {
                log.warn("未获取到任何热点话题数据");
                return;
            }

            // 2. 去重和排序
            List<HotTopic> uniqueTopics = removeDuplicates(allTopics);
            List<HotTopic> top5Topics = uniqueTopics.stream()
                    .sorted(Comparator.comparing(HotTopic::getHotValue).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            log.info("筛选出{}条热门话题进行文章生成", top5Topics.size());

            // 3. 生成文章任务
            if (!top5Topics.isEmpty()) {
                try {
                    String prompt = generatePrompt(top5Topics);
                    ArticleTask task = new ArticleTask()
                            .setPrompt(prompt)
                            .setArticleCategoryId(1L)
                            .setCreatedBy("system");

                    aiArticleGenerator.generateArticle(task);
                    log.info("成功生成热点文章任务");
                } catch (Exception e) {
                    log.error("生成文章任务失败: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("热点话题爬取任务执行异常: {}", e.getMessage(), e);
        }
        log.info("热点话题爬取任务执行完成");
    }

    @Retryable(value = {HttpClientErrorException.class, ResourceAccessException.class},
              maxAttempts = 3, backoff = @Backoff(delay = 2000))
    private List<HotTopic> crawlZhihuHotTopics() {
        try {
            log.info("开始爬取知乎热榜数据");
            ResponseEntity<String> response = restTemplate.getForEntity(ZHIHU_API, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            List<HotTopic> topics = new ArrayList<>();

            JsonNode data = root.path("data");
            if (data.isMissingNode() || !data.isArray()) {
                log.error("知乎API返回数据格式异常: {}", response.getBody());
                return Collections.emptyList();
            }

            for (JsonNode item : data) {
                try {
                    JsonNode target = item.path("target");
                    topics.add(new HotTopic(
                        target.path("title").asText(),
                        target.path("excerpt").asText(),
                        target.path("heat").asLong(),
                        "zhihu"
                    ));
                } catch (Exception e) {
                    log.warn("解析知乎热榜数据项失败: {}", e.getMessage());
                }
            }
            log.info("成功获取{}条知乎热榜数据", topics.size());
            return topics;
        } catch (HttpClientErrorException e) {
            log.error("请求知乎API失败: {}, 状态码: {}", e.getMessage(), e.getStatusCode());
            throw e;
        } catch (ResourceAccessException e) {
            log.error("网络连接异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("爬取知乎热榜数据异常: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Retryable(value = {HttpClientErrorException.class, ResourceAccessException.class},
              maxAttempts = 3, backoff = @Backoff(delay = 2000))
    private List<HotTopic> crawlToutiaoHotTopics() {
        try {
            log.info("开始爬取头条热榜数据");
            ResponseEntity<String> response = restTemplate.getForEntity(TOUTIAO_API, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            List<HotTopic> topics = new ArrayList<>();

            JsonNode data = root.path("data");
            if (data.isMissingNode() || !data.isArray()) {
                log.error("头条API返回数据格式异常: {}", response.getBody());
                return Collections.emptyList();
            }

            for (JsonNode item : data) {
                try {
                    topics.add(new HotTopic(
                        item.path("Title").asText(),
                        item.path("Abstract").asText(),
                        item.path("HotValue").asLong(),
                        "toutiao"
                    ));
                } catch (Exception e) {
                    log.warn("解析头条热榜数据项失败: {}", e.getMessage());
                }
            }
            log.info("成功获取{}条头条热榜数据", topics.size());
            return topics;
        } catch (HttpClientErrorException e) {
            log.error("请求头条API失败: {}, 状态码: {}", e.getMessage(), e.getStatusCode());
            throw e;
        } catch (ResourceAccessException e) {
            log.error("网络连接异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("爬取头条热榜数据异常: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private List<HotTopic> crawlWeiboHotTopics() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(WEIBO_API, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            List<HotTopic> topics = new ArrayList<>();

            JsonNode data = root.path("data").path("realtime");
            for (JsonNode item : data) {
                topics.add(new HotTopic(
                    item.path("note").asText(),
                    "",  // 微博热搜可能没有详细描述
                    item.path("raw_hot").asLong(),
                    "weibo"
                ));
            }
            return topics;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<HotTopic> crawlBaiduHotTopics() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(BAIDU_API, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            List<HotTopic> topics = new ArrayList<>();

            JsonNode data = root.path("data").path("cards");
            for (JsonNode item : data) {
                topics.add(new HotTopic(
                    item.path("title").asText(),
                    item.path("desc").asText(),
                    item.path("hot_score").asLong(),
                    "baidu"
                ));
            }
            return topics;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<HotTopic> removeDuplicates(List<HotTopic> topics) {
        Map<String, HotTopic> uniqueTopics = new HashMap<>();
        
        for (HotTopic topic : topics) {
            String normalizedTitle = normalizeText(topic.getTitle());
            if (!uniqueTopics.containsKey(normalizedTitle)) {
                uniqueTopics.put(normalizedTitle, topic);
            } else {
                // 如果已存在，保留热度更高的
                HotTopic existing = uniqueTopics.get(normalizedTitle);
                if (topic.getHotValue() > existing.getHotValue()) {
                    uniqueTopics.put(normalizedTitle, topic);
                }
            }
        }
        
        return new ArrayList<>(uniqueTopics.values());
    }

    private String normalizeText(String text) {
        // 简单的文本标准化处理
        return text.toLowerCase()
                .replaceAll("[\\p{P}\\s]+", "") // 移除标点和空白
                .trim();
    }

    private String generatePrompt(List<HotTopic> topics) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请基于以下热点话题，撰写一篇深度分析文章，需要：\n");
        prompt.append("1. 分析这些话题的共同点和社会意义\n");
        prompt.append("2. 对这些现象进行深入的思考和讨论\n");
        prompt.append("3. 总结这些话题反映的社会趋势\n\n");
        prompt.append("热点话题：\n");

        for (int i = 0; i < topics.size(); i++) {
            HotTopic topic = topics.get(i);
            prompt.append(i + 1).append(". ").append(topic.getTitle());
            if (!topic.getDescription().isEmpty()) {
                prompt.append("：").append(topic.getDescription());
            }
            prompt.append("\n");
        }

        return prompt.toString();
    }

    private static class HotTopic {
        private final String title;
        private final String description;
        private final long hotValue;
        private final String source;

        public HotTopic(String title, String description, long hotValue, String source) {
            this.title = title;
            this.description = description;
            this.hotValue = hotValue;
            this.source = source;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public long getHotValue() {
            return hotValue;
        }

        public String getSource() {
            return source;
        }
    }
}