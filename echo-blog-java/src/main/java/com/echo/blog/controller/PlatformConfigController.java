package com.echo.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echo.blog.common.api.ApiResult;
import com.echo.blog.entity.PlatformConfig;
import com.echo.blog.model.dto.PlatformConfigDTO;
import com.echo.blog.model.vo.PlatformConfigVO;
import com.echo.blog.security.RequirePermission;
import com.echo.blog.service.PlatformConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 平台配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/platform-configs")
@RequiredArgsConstructor
@Tag(name = "平台配置", description = "平台配置相关接口")
public class PlatformConfigController {

    private final PlatformConfigService platformConfigService;

    @PostMapping
    @Operation(summary = "创建平台配置")
    @RequirePermission("platform:config:create")
    public ApiResult<PlatformConfigVO> createConfig(@RequestBody PlatformConfigDTO configDTO, Authentication authentication) {
        PlatformConfig config = new PlatformConfig();
        BeanUtils.copyProperties(configDTO, config);
        
        // 设置用户ID
        Long userId = Long.parseLong(authentication.getName());
        config.setUserId(userId);
        
        PlatformConfig createdConfig = platformConfigService.createConfig(config);
        PlatformConfigVO configVO = convertToVO(createdConfig);
        
        return ApiResult.success(configVO);
    }

    @GetMapping
    @Operation(summary = "获取当前用户的平台配置列表")
    @RequirePermission("platform:config:list")
    public ApiResult<Page<PlatformConfigVO>> getConfigList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        
        Long userId = Long.parseLong(authentication.getName());
        Page<PlatformConfig> page = new Page<>(current, size);
        Page<PlatformConfig> configPage = platformConfigService.getConfigsByUser(userId, page);
        
        List<PlatformConfigVO> configVOList = configPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        Page<PlatformConfigVO> resultPage = new Page<>(configPage.getCurrent(), configPage.getSize(), configPage.getTotal());
        resultPage.setRecords(configVOList);
        
        return ApiResult.success(resultPage);
    }

    @GetMapping("/enabled")
    @Operation(summary = "获取当前用户的启用平台配置")
    @RequirePermission("platform:config:list")
    public ApiResult<List<PlatformConfigVO>> getEnabledConfigs(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<PlatformConfig> configs = platformConfigService.getEnabledConfigsByUser(userId);
        List<PlatformConfigVO> configVOList = configs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return ApiResult.success(configVOList);
    }

    @GetMapping("/{configId}")
    @Operation(summary = "获取平台配置详情")
    @RequirePermission("platform:config:view")
    public ApiResult<PlatformConfigVO> getConfigDetail(@PathVariable Long configId) {
        PlatformConfig config = platformConfigService.getConfigById(configId);
        if (config == null) {
            return ApiResult.failed("配置不存在");
        }
        
        PlatformConfigVO configVO = convertToVO(config);
        return ApiResult.success(configVO);
    }

    @PutMapping("/{configId}")
    @Operation(summary = "更新平台配置")
    @RequirePermission("platform:config:update")
    public ApiResult<PlatformConfigVO> updateConfig(@PathVariable Long configId, @RequestBody PlatformConfigDTO configDTO) {
        PlatformConfig config = platformConfigService.getConfigById(configId);
        if (config == null) {
            return ApiResult.failed("配置不存在");
        }
        
        BeanUtils.copyProperties(configDTO, config);
        PlatformConfig updatedConfig = platformConfigService.updateConfig(config);
        PlatformConfigVO configVO = convertToVO(updatedConfig);
        
        return ApiResult.success(configVO);
    }

    @PutMapping("/{configId}/status")
    @Operation(summary = "更新平台配置状态")
    @RequirePermission("platform:config:update")
    public ApiResult<PlatformConfigVO> updateConfigStatus(@PathVariable Long configId, @RequestParam String status) {
        PlatformConfig updatedConfig = platformConfigService.updateConfigStatus(configId, status);
        if (updatedConfig == null) {
            return ApiResult.failed("配置不存在");
        }
        
        PlatformConfigVO configVO = convertToVO(updatedConfig);
        return ApiResult.success(configVO);
    }

    @DeleteMapping("/{configId}")
    @Operation(summary = "删除平台配置")
    @RequirePermission("platform:config:delete")
    public ApiResult<Boolean> deleteConfig(@PathVariable Long configId) {
        boolean result = platformConfigService.deleteConfig(configId);
        return result ? ApiResult.success(true) : ApiResult.failed("删除失败");
    }

    /**
     * 将实体转换为VO
     */
    private PlatformConfigVO convertToVO(PlatformConfig config) {
        PlatformConfigVO vo = new PlatformConfigVO();
        BeanUtils.copyProperties(config, vo);
        // 敏感信息处理
        vo.setAuthCredential("[PROTECTED]");
        vo.setRefreshToken("[PROTECTED]");
        return vo;
    }
} 