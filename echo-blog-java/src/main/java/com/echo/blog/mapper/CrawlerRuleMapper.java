package com.echo.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echo.blog.entity.CrawlerRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 爬虫规则Mapper接口
 */
@Mapper
public interface CrawlerRuleMapper extends BaseMapper<CrawlerRule> {
    
    /**
     * 查询启用的爬虫规则
     * @return 启用的爬虫规则列表
     */
    @Select("SELECT * FROM a_crawler_rule WHERE status = 'ENABLED' ORDER BY created_time DESC")
    List<CrawlerRule> findEnabledRules();
    
    /**
     * 查询用户创建的爬虫规则
     * @param userId 用户ID
     * @return 爬虫规则列表
     */
    @Select("SELECT * FROM a_crawler_rule WHERE created_by = #{userId} ORDER BY created_time DESC")
    List<CrawlerRule> findRulesByUser(@Param("userId") String userId);
} 