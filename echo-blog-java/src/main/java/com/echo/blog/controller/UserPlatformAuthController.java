package com.echo.blog.controller;

import com.echo.blog.common.api.ApiResult;
import com.echo.blog.model.dto.PlatformAuthDTO;
import com.echo.blog.model.vo.UserPlatformAuthVO;
import com.echo.blog.security.RequirePermission;
import com.echo.blog.service.UserPlatformAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户平台授权控制器
 */
@RestController
@RequestMapping("/api/user/platforms")
@RequiredArgsConstructor
@Tag(name = "用户平台授权", description = "用户平台授权相关接口")
public class UserPlatformAuthController {
    
    private final UserPlatformAuthService userPlatformAuthService;
    
    /**
     * 获取当前用户的平台授权列表
     *
     * @param authentication 认证信息
     * @return 平台授权列表
     */
    @GetMapping
    @Operation(summary = "获取当前用户的平台授权列表")
    public ApiResult<List<UserPlatformAuthVO>> getCurrentUserPlatformAuths(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = Long.valueOf(userDetails.getUsername());
        return ApiResult.success(userPlatformAuthService.getUserPlatformAuths(userId));
    }
    
    /**
     * 添加平台授权
     *
     * @param authDTO 授权信息
     * @param authentication 认证信息
     * @return 授权信息
     */
    @PostMapping
    @Operation(summary = "添加平台授权")
    public ApiResult<UserPlatformAuthVO> addPlatformAuth(
            @RequestBody @Valid PlatformAuthDTO authDTO,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = Long.valueOf(userDetails.getUsername());
        return ApiResult.success(userPlatformAuthService.addPlatformAuth(userId, authDTO));
    }
    
    /**
     * 更新平台授权
     *
     * @param authId 授权ID
     * @param authDTO 授权信息
     * @return 授权信息
     */
    @PutMapping("/{authId}")
    @Operation(summary = "更新平台授权")
    @RequirePermission("user:platform:update")
    public ApiResult<UserPlatformAuthVO> updatePlatformAuth(
            @PathVariable Long authId,
            @RequestBody @Valid PlatformAuthDTO authDTO) {
        return ApiResult.success(userPlatformAuthService.updatePlatformAuth(authId, authDTO));
    }
    
    /**
     * 删除平台授权
     *
     * @param authId 授权ID
     * @return 是否成功
     */
    @DeleteMapping("/{authId}")
    @Operation(summary = "删除平台授权")
    @RequirePermission("user:platform:delete")
    public ApiResult<Boolean> deletePlatformAuth(@PathVariable Long authId) {
        return ApiResult.success(userPlatformAuthService.deletePlatformAuth(authId));
    }
} 