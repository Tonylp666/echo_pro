package com.echo.blog.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户VO
 */
@Data
public class UserVO {
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 角色列表
     */
    private List<String> roles;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 获取用户名称，兼容旧代码
     */
    public String getName() {
        return this.username;
    }
} 