package com.echo.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echo.blog.entity.PlatformConfig;

import java.util.List;

/**
 * 平台配置服务接口
 */
public interface PlatformConfigService {
    
    /**
     * 创建平台配置
     * @param config 配置信息
     * @return 创建的配置
     */
    PlatformConfig createConfig(PlatformConfig config);
    
    /**
     * 更新平台配置
     * @param config 配置信息
     * @return 更新后的配置
     */
    PlatformConfig updateConfig(PlatformConfig config);
    
    /**
     * 获取用户的平台配置
     * @param userId 用户ID
     * @return 平台配置列表
     */
    List<PlatformConfig> getEnabledConfigsByUser(Long userId);
    
    /**
     * 获取用户特定平台的配置
     * @param userId 用户ID
     * @param platformType 平台类型
     * @return 平台配置
     */
    PlatformConfig getConfigByUserAndPlatform(Long userId, String platformType);
    
    /**
     * 获取用户的平台配置（分页）
     * @param userId 用户ID
     * @param page 分页参数
     * @return 平台配置分页列表
     */
    Page<PlatformConfig> getConfigsByUser(Long userId, Page<PlatformConfig> page);
    
    /**
     * 获取平台配置详情
     * @param configId 配置ID
     * @return 配置详情
     */
    PlatformConfig getConfigById(Long configId);
    
    /**
     * 启用/禁用平台配置
     * @param configId 配置ID
     * @param status 状态
     * @return 更新后的配置
     */
    PlatformConfig updateConfigStatus(Long configId, String status);
    
    /**
     * 删除平台配置
     * @param configId 配置ID
     * @return 是否成功
     */
    boolean deleteConfig(Long configId);
} 