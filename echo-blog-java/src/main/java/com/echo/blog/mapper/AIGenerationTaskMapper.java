package com.echo.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echo.blog.entity.AIGenerationTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI内容生成任务Mapper接口
 */
@Mapper
public interface AIGenerationTaskMapper extends BaseMapper<AIGenerationTask> {
    
    /**
     * 查询待处理的任务
     * @param limit 限制数量
     * @return 待处理任务列表
     */
    @Select("SELECT * FROM a_ai_generation_task WHERE status = 'PENDING' ORDER BY created_time ASC LIMIT #{limit}")
    List<AIGenerationTask> findPendingTasks(@Param("limit") int limit);
    
    /**
     * 查询用户的任务列表
     * @param userId 用户ID
     * @return 任务列表
     */
    @Select("SELECT * FROM a_ai_generation_task WHERE created_by = #{userId} ORDER BY created_time DESC")
    List<AIGenerationTask> findTasksByUser(@Param("userId") String userId);
} 