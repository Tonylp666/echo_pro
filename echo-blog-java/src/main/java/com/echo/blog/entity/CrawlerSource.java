package com.echo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("a_crawler_source")
public class CrawlerSource {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String sourceChannel;
    
    private String url;
    
    private String cookie;
    
    private String userId;
    
    private String userName;
    
    private Integer topK;
    
    private String status;
    
    private String createdBy;
    
    private LocalDateTime createdTime;
    
    private LocalDateTime updatedTime;
}