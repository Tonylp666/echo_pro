package com.echo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 文章子表实体类
 */
@Data
@Accessors(chain = true)
@TableName("a_article_sub")
public class ArticleSub {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文章ID
     */
    private Long articleId;
    
    /**
     * 子类型
     */
    private String subType;
    
    /**
     * 子ID
     */
    private String subId;
    
    /**
     * 子URL
     */
    private String subUrl;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 平台元数据
     */
    private String platformMeta;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 获取同步状态
     * @return 同步状态
     */
    public String getSyncStatus() {
        return this.status;
    }
}