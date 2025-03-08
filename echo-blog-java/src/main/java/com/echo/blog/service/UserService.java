package com.echo.blog.service;

import com.echo.blog.entity.User;
import com.echo.blog.model.dto.RegisterDTO;
import com.echo.blog.model.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户
     */
    User findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 注册用户
     *
     * @param registerDTO 注册信息
     * @return 用户信息
     */
    UserVO register(RegisterDTO registerDTO);
    
    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserVO getCurrentUser();
}