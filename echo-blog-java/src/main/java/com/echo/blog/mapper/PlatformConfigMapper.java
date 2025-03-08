package com.echo.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echo.blog.entity.PlatformConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 平台配置Mapper接口
 */
@Mapper
public interface PlatformConfigMapper extends BaseMapper<PlatformConfig> {
    
    /**
     * 查询用户的平台配置
     * @param userId 用户ID
     * @return 平台配置列表
     */
    @Select("SELECT * FROM a_platform_config WHERE user_id = #{userId} AND status = 'ENABLED' ORDER BY created_time DESC")
    List<PlatformConfig> findEnabledConfigsByUser(@Param("userId") Long userId);
    
    /**
     * 查询用户特定平台的配置
     * @param userId 用户ID
     * @param platformType 平台类型
     * @return 平台配置
     */
    @Select("SELECT * FROM a_platform_config WHERE user_id = #{userId} AND platform_type = #{platformType} AND status = 'ENABLED' LIMIT 1")
    PlatformConfig findConfigByUserAndPlatform(@Param("userId") Long userId, @Param("platformType") String platformType);
} 