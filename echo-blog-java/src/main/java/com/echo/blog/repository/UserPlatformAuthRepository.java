package com.echo.blog.repository;

import com.echo.blog.entity.UserPlatformAuth;
import com.echo.blog.enums.PlatformType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户平台授权仓库
 */
@Repository
public interface UserPlatformAuthRepository extends JpaRepository<UserPlatformAuth, Long> {
    
    /**
     * 根据用户ID查询授权列表
     *
     * @param userId 用户ID
     * @return 授权列表
     */
    List<UserPlatformAuth> findByUserId(Long userId);
    
    /**
     * 根据用户ID和平台类型查询授权
     *
     * @param userId 用户ID
     * @param platformType 平台类型
     * @return 授权信息
     */
    UserPlatformAuth findByUserIdAndPlatformType(Long userId, PlatformType platformType);
    
    /**
     * 根据用户ID和平台类型字符串查询授权
     *
     * @param userId 用户ID
     * @param platformType 平台类型字符串
     * @return 授权信息
     */
    @Query("SELECT u FROM UserPlatformAuth u WHERE u.userId = :userId AND u.platformType = :#{T(com.echo.blog.enums.PlatformType).valueOf(#platformType)}")
    UserPlatformAuth findByUserIdAndPlatformTypeString(@Param("userId") Long userId, @Param("platformType") String platformType);
} 