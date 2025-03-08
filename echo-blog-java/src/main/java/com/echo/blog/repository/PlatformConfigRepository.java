package com.echo.blog.repository;

import com.echo.blog.entity.PlatformConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 平台配置仓库接口
 */
@Repository
public interface PlatformConfigRepository extends JpaRepository<PlatformConfig, Long> {
    
    /**
     * 根据平台类型查询配置
     *
     * @param platformType 平台类型
     * @return 平台配置
     */
    PlatformConfig findByPlatformType(String platformType);
    
    /**
     * 根据用户ID查询配置
     *
     * @param userId 用户ID
     * @return 平台配置列表
     */
    List<PlatformConfig> findByUserId(Long userId);
    
    /**
     * 根据用户ID和平台类型查询配置
     *
     * @param userId 用户ID
     * @param platformType 平台类型
     * @return 平台配置
     */
    PlatformConfig findByUserIdAndPlatformType(Long userId, String platformType);
    
    /**
     * 查询启用的平台配置
     *
     * @return 平台配置列表
     */
    @Query("SELECT p FROM PlatformConfig p WHERE p.enabled = true")
    List<PlatformConfig> findEnabledConfigs();
    
    /**
     * 查询用户启用的平台配置
     *
     * @param userId 用户ID
     * @return 平台配置列表
     */
    @Query("SELECT p FROM PlatformConfig p WHERE p.userId = :userId AND p.enabled = true")
    List<PlatformConfig> findEnabledConfigsByUser(Long userId);
} 