package com.echo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 文章实体类
 */
@Data
@Accessors(chain = true)
@TableName("a_article")
public class Article {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文章分类ID
     */
    private Long articleCategoryId;
    
    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 文章内容
     */
    private String content;
    
    /**
     * 文章HTML内容
     */
    private String contentHtml;
    
    /**
     * 文章Markdown内容
     */
    private String contentMarkdown;
    
    /**
     * 文章图片OSS键值
     */
    private String ossKeys;
    
    /**
     * 文章摘要
     */
    private String summary;
    
    /**
     * 文章状态
     */
    private String status;
    
    /**
     * 作者
     */
    private String createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 浏览次数
     */
    private Long viewCount;
    
    /**
     * 点赞次数
     */
    private Long likeCount;
    
    /**
     * 封面图片
     */
    private String coverImage;
}