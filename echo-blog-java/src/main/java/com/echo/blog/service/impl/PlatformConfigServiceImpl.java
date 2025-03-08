package com.echo.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echo.blog.entity.PlatformConfig;
import com.echo.blog.mapper.PlatformConfigMapper;
import com.echo.blog.service.PlatformConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 平台配置服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformConfigServiceImpl extends ServiceImpl<PlatformConfigMapper, PlatformConfig> implements PlatformConfigService {

    private final PlatformConfigMapper platformConfigMapper;

    @Override
    @Transactional
    public PlatformConfig createConfig(PlatformConfig config) {
        config.setStatus("ENABLED")
              .setCreatedTime(LocalDateTime.now())
              .setUpdatedTime(LocalDateTime.now());
        save(config);
        return config;
    }

    @Override
    @Transactional
    public PlatformConfig updateConfig(PlatformConfig config) {
        config.setUpdatedTime(LocalDateTime.now());
        updateById(config);
        return config;
    }

    @Override
    public List<PlatformConfig> getEnabledConfigsByUser(Long userId) {
        return platformConfigMapper.findEnabledConfigsByUser(userId);
    }

    @Override
    public PlatformConfig getConfigByUserAndPlatform(Long userId, String platformType) {
        return platformConfigMapper.findConfigByUserAndPlatform(userId, platformType);
    }

    @Override
    public Page<PlatformConfig> getConfigsByUser(Long userId, Page<PlatformConfig> page) {
        LambdaQueryWrapper<PlatformConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PlatformConfig::getUserId, userId)
                   .orderByDesc(PlatformConfig::getCreatedTime);
        return page(page, queryWrapper);
    }

    @Override
    public PlatformConfig getConfigById(Long configId) {
        return getById(configId);
    }

    @Override
    @Transactional
    public PlatformConfig updateConfigStatus(Long configId, String status) {
        PlatformConfig config = getById(configId);
        if (config == null) {
            return null;
        }
        
        config.setStatus(status)
              .setUpdatedTime(LocalDateTime.now());
        updateById(config);
        return config;
    }

    @Override
    @Transactional
    public boolean deleteConfig(Long configId) {
        return removeById(configId);
    }
} 