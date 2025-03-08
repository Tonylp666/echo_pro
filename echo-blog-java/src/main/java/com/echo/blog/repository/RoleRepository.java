package com.echo.blog.repository;

import com.echo.blog.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 角色仓库
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * 根据角色名查找角色
     *
     * @param name 角色名
     * @return 角色
     */
    Role findByName(String name);
} 