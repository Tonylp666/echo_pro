# Echo Blog Platform - Backend

## 项目概述

Echo Blog 是一个功能丰富的内容创作与分发平台，支持用户原创内容创作以及AI辅助内容生成。平台集成了多种内容来源，包括爬虫采集、搜索引擎结果整合以及热点话题追踪，并支持一键分发到多个主流内容平台。

## 技术栈

- Java 21
- Spring Boot 3.x
- MySQL 8.0
- MyBatis-Plus
- Spring Security
- DeepSeek API (AI内容生成)
- 阿里云百炼SDK (AI内容生成)
- Bing Search API (搜索功能)

## 核心功能

### 内容创作与管理
- 支持Markdown格式的文章编辑与预览
- 文章分类管理
- 文章状态管理（草稿、已发布、私密等）
- 文章统计（浏览量、点赞数）

### AI辅助内容生成
- 基于爬虫数据的内容生成
- 基于搜索关键词的内容生成
- 基于热点话题的内容生成
- 支持多种AI模型（DeepSeek、阿里云百炼）
- 任务管理与监控

### 爬虫系统
- 自定义爬虫规则配置
- 目标网站内容抓取
- 数据清洗与结构化
- 爬虫任务管理

### 内容分发
- 支持微信公众号、知乎、今日头条、小红书等平台
- 平台账号授权管理
- 内容格式自动适配
- 发布状态跟踪

### 用户系统
- 用户注册与登录
- 基于角色的权限控制
- 个人资料管理

## 系统架构

```
com.echo.blog
├── common        // 通用工具类和常量
├── config        // 系统配置
├── controller    // 控制器层
├── entity        // 实体类
├── enums         // 枚举类型
├── exception     // 异常处理
├── mapper        // MyBatis映射接口
├── model         // 数据传输对象
├── repository    // 数据访问层
├── security      // 安全配置
└── service       // 业务逻辑层
```

## 数据模型

### 核心实体
- User: 用户信息
- Role/Permission: 角色与权限
- Article: 文章内容
- ArticleCategory: 文章分类
- CrawlerRule: 爬虫规则
- AIGenerationTask: AI生成任务
- UserPlatformAuth: 用户平台授权

## 开发环境设置

### 前提条件
- JDK 21
- Maven 3.8+
- MySQL 8.0

### 数据库配置
1. 创建MySQL数据库 `echo_blog`
2. 配置 `application.properties` 中的数据库连接信息

### API密钥配置
在 `application.properties` 中配置以下API密钥:
- DeepSeek API Key
- 阿里云百炼SDK配置
- Bing Search API Key

### 构建与运行
```bash
# 克隆项目
git clone https://your-repository/echo-blog.git

# 进入项目目录
cd echo-blog-java

# 构建项目
mvn clean package

# 运行项目
java -jar target/echo-blog-0.0.1-SNAPSHOT.jar
```

## API文档

启动应用后，可通过以下地址访问Swagger API文档:
```
http://localhost:8080/swagger-ui.html
```

## 部署指南

### 生产环境配置
1. 配置 `application-prod.properties` 文件
2. 使用以下命令启动:
```bash
java -jar echo-blog.jar --spring.profiles.active=prod
```

### 安全配置
- 确保生产环境中使用HTTPS
- 配置适当的CORS策略
- 启用Spring Security的CSRF保护

## 许可证

[MIT License](LICENSE) 