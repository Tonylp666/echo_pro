package com.echo.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echo.blog.entity.CrawlerSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CrawlerSourceMapper extends BaseMapper<CrawlerSource> {
    
    @Select("SELECT * FROM a_crawler_source WHERE source_channel = #{channel} AND status = 'ACTIVE'")
    List<CrawlerSource> findByChannel(@Param("channel") String channel);
    
    @Select("SELECT * FROM a_crawler_source WHERE status = #{status} ORDER BY created_time DESC")
    List<CrawlerSource> findByStatus(@Param("status") String status);
    
    @Select("SELECT * FROM a_crawler_source WHERE user_id = #{userId} AND status = 'ACTIVE'")
    List<CrawlerSource> findByUserId(@Param("userId") String userId);
}