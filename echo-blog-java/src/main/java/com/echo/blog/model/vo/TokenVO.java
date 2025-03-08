package com.echo.blog.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 令牌VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {
    
    /**
     * 令牌
     */
    private String token;
} 