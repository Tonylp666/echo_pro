-- 文章分类表
CREATE TABLE IF NOT EXISTS `a_article_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(200) COMMENT '分类描述',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `created_by` VARCHAR(50) NOT NULL COMMENT '创建者',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

-- 文章表
CREATE TABLE IF NOT EXISTS `a_article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `article_category_id` BIGINT NOT NULL COMMENT '文章分类ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `content` TEXT NOT NULL COMMENT '文章内容',
    `content_html` TEXT COMMENT '文章HTML内容',
    `content_markdown` TEXT COMMENT '文章Markdown内容',
    `oss_keys` TEXT COMMENT '文章图片OSS键值',
    `summary` VARCHAR(500) COMMENT '文章摘要',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `created_by` VARCHAR(50) NOT NULL COMMENT '作者',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    `view_count` BIGINT DEFAULT 0 COMMENT '浏览次数',
    `like_count` BIGINT DEFAULT 0 COMMENT '点赞次数',
    `cover_image` VARCHAR(500) COMMENT '封面图片',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`article_category_id`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 为文章表添加全文索引
ALTER TABLE `a_article` ADD FULLTEXT INDEX `ft_article` (`title`, `content`);

-- 文章子表（用于文章同步）
CREATE TABLE IF NOT EXISTS `a_article_sub` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `sub_type` VARCHAR(20) NOT NULL COMMENT '子类型',
    `sub_id` VARCHAR(100) COMMENT '子ID',
    `sub_url` VARCHAR(500) COMMENT '子URL',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `platform_meta` VARCHAR(500) COMMENT '平台元数据',
    `error_message` TEXT COMMENT '错误信息',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_article` (`article_id`),
    KEY `idx_sub_type` (`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章同步子表';

-- 用户表
CREATE TABLE IF NOT EXISTS `a_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `name` VARCHAR(50) COMMENT '姓名',
    `avatar` VARCHAR(200) COMMENT '头像',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `a_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(200) COMMENT '角色描述',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `a_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 权限表
CREATE TABLE IF NOT EXISTS `a_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `code` VARCHAR(50) NOT NULL COMMENT '权限编码',
    `type` VARCHAR(20) NOT NULL COMMENT '权限类型',
    `parent_id` BIGINT COMMENT '父权限ID',
    `path` VARCHAR(200) COMMENT '权限路径',
    `component` VARCHAR(200) COMMENT '前端组件',
    `icon` VARCHAR(50) COMMENT '图标',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `a_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 文章自动生成任务表
CREATE TABLE IF NOT EXISTS `a_article_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
    `description` TEXT COMMENT '任务描述',
    `article_category_id` BIGINT NOT NULL COMMENT '文章分类ID',
    `prompt` TEXT NOT NULL COMMENT '生成提示词',
    `source_data` TEXT COMMENT '源数据',
    `source_type` VARCHAR(20) COMMENT '源类型',
    `result_article_id` BIGINT COMMENT '结果文章ID',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `error_message` TEXT COMMENT '错误信息',
    `created_by` VARCHAR(50) NOT NULL COMMENT '创建者',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    `title` VARCHAR(200) COMMENT '标题',
    `schedule_type` VARCHAR(20) COMMENT '调度类型',
    `schedule_cron` VARCHAR(50) COMMENT 'CRON表达式',
    `last_execute_time` DATETIME COMMENT '最后执行时间',
    `next_execute_time` DATETIME COMMENT '下次执行时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`article_category_id`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章自动生成任务表';

-- 爬虫源配置表
CREATE TABLE IF NOT EXISTS `a_crawler_source` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `source_channel` VARCHAR(50) NOT NULL COMMENT '来源渠道',
    `url` VARCHAR(500) NOT NULL COMMENT '爬取URL',
    `cookie` TEXT COMMENT 'Cookie信息',
    `top_k` INT DEFAULT 10 COMMENT '获取条数',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `created_by` VARCHAR(50) NOT NULL COMMENT '创建者',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_channel` (`source_channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='爬虫源配置表';

-- 用户平台授权表
CREATE TABLE IF NOT EXISTS `a_user_platform_auth` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `platform_type` VARCHAR(20) NOT NULL COMMENT '平台类型',
    `auth_token` VARCHAR(500) NOT NULL COMMENT '授权令牌',
    `expire_time` DATETIME COMMENT '过期时间',
    `refresh_token` VARCHAR(500) COMMENT '刷新令牌',
    `platform_user_id` VARCHAR(100) COMMENT '平台用户ID',
    `platform_user_name` VARCHAR(100) COMMENT '平台用户名',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_platform` (`user_id`, `platform_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户平台授权表';

-- AI内容生成任务表
CREATE TABLE IF NOT EXISTS `a_ai_generation_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_type` VARCHAR(20) NOT NULL COMMENT '任务类型',
    `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
    `description` VARCHAR(500) COMMENT '任务描述',
    `source_data` TEXT COMMENT '源数据',
    `prompt` TEXT NOT NULL COMMENT '提示词',
    `ai_model` VARCHAR(50) NOT NULL COMMENT 'AI模型',
    `result_article_id` BIGINT COMMENT '生成的文章ID',
    `status` VARCHAR(20) NOT NULL COMMENT '任务状态',
    `error_message` TEXT COMMENT '错误信息',
    `created_by` VARCHAR(50) NOT NULL COMMENT '创建者',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_created_by` (`created_by`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI内容生成任务表';

-- 爬虫规则表
CREATE TABLE IF NOT EXISTS `a_crawler_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `target_url` VARCHAR(500) NOT NULL COMMENT '目标网站URL',
    `list_selector` VARCHAR(200) COMMENT '列表选择器',
    `title_selector` VARCHAR(200) COMMENT '标题选择器',
    `content_selector` VARCHAR(200) COMMENT '内容选择器',
    `date_selector` VARCHAR(200) COMMENT '日期选择器',
    `author_selector` VARCHAR(200) COMMENT '作者选择器',
    `headers` TEXT COMMENT '请求头（JSON格式）',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `created_by` VARCHAR(50) NOT NULL COMMENT '创建者',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_created_by` (`created_by`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='爬虫规则表';

-- 平台配置表
CREATE TABLE IF NOT EXISTS `a_platform_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `platform_type` VARCHAR(20) NOT NULL COMMENT '平台类型',
    `platform_name` VARCHAR(50) NOT NULL COMMENT '平台名称',
    `platform_icon` VARCHAR(200) COMMENT '平台图标',
    `auth_credential` TEXT NOT NULL COMMENT '授权凭证',
    `expire_time` DATETIME COMMENT '授权过期时间',
    `refresh_token` VARCHAR(500) COMMENT '刷新令牌',
    `extra_config` TEXT COMMENT '额外配置（JSON格式）',
    `status` VARCHAR(20) NOT NULL COMMENT '状态',
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_platform_type` (`platform_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台配置表';