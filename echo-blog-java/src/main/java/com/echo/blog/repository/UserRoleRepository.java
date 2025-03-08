package com.echo.blog.repository;

import com.echo.blog.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色仓库
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    /**
     * 根据用户ID查找用户角色
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    List<UserRole> findByUserId(Long userId);
    
    /**
     * 根据角色ID查找用户角色
     *
     * @param roleId 角色ID
     * @return 用户角色列表
     */
    List<UserRole> findByRoleId(Long roleId);
    
    /**
     * 根据用户ID和角色ID查找用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 用户角色
     */
    UserRole findByUserIdAndRoleId(Long userId, Long roleId);
} 