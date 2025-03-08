package com.echo.blog.service;

import com.echo.blog.entity.UserPlatformAuth;
import com.echo.blog.enums.PlatformType;
import com.echo.blog.model.dto.PlatformAuthDTO;
import com.echo.blog.model.vo.UserPlatformAuthVO;

import java.util.List;

/**
 * 用户平台授权服务接口
 */
public interface UserPlatformAuthService {
    
    /**
     * 获取用户的平台授权列表
     *
     * @param userId 用户ID
     * @return 平台授权列表
     */
    List<UserPlatformAuthVO> getUserPlatformAuths(Long userId);
    
    /**
     * 添加平台授权
     *
     * @param userId 用户ID
     * @param authDTO 授权信息
     * @return 授权信息
     */
    UserPlatformAuthVO addPlatformAuth(Long userId, PlatformAuthDTO authDTO);
    
    /**
     * 更新平台授权
     *
     * @param authId 授权ID
     * @param authDTO 授权信息
     * @return 授权信息
     */
    UserPlatformAuthVO updatePlatformAuth(Long authId, PlatformAuthDTO authDTO);
    
    /**
     * 删除平台授权
     *
     * @param authId 授权ID
     * @return 是否成功
     */
    boolean deletePlatformAuth(Long authId);
    
    /**
     * 获取用户在特定平台的授权信息
     *
     * @param userId 用户ID
     * @param platformType 平台类型
     * @return 授权信息
     */
    UserPlatformAuth getUserPlatformAuth(Long userId, PlatformType platformType);
    
    /**
     * 获取用户在特定平台的授权信息（字符串类型）
     *
     * @param userId 用户ID
     * @param platformType 平台类型字符串
     * @return 授权信息
     */
    UserPlatformAuth getUserPlatformAuth(Long userId, String platformType);
} 