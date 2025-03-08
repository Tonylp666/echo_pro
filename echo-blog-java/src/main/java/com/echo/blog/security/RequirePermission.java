package com.echo.blog.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限检查注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    
    /**
     * 所需权限
     */
    String value();

    /**
     * 权限校验模式（AND/OR）
     */
    Logical logical() default Logical.AND;

    enum Logical {
        AND, OR
    }
}