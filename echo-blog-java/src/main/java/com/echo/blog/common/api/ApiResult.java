package com.echo.blog.common.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * API 统一返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 成功
     */
    public static <T> ApiResult<T> success() {
        return success(null);
    }
    
    /**
     * 成功
     */
    public static <T> ApiResult<T> success(T data) {
        return success(data, "操作成功");
    }
    
    /**
     * 成功
     */
    public static <T> ApiResult<T> success(T data, String message) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
    
    /**
     * 失败
     */
    public static <T> ApiResult<T> error() {
        return error("操作失败");
    }
    
    /**
     * 失败
     */
    public static <T> ApiResult<T> error(String message) {
        return error(message, 500);
    }
    
    /**
     * 失败
     */
    public static <T> ApiResult<T> error(String message, Integer code) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> ApiResult<T> validateFailed(String message) {
        return new ApiResult<>(400, message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> ApiResult<T> unauthorized(String message) {
        return new ApiResult<>(401, message, null);
    }

    /**
     * 未授权返回结果
     */
    public static <T> ApiResult<T> forbidden(String message) {
        return new ApiResult<>(403, message, null);
    }
    
    /**
     * 操作失败返回结果
     */
    public static <T> ApiResult<T> failed(String message) {
        return new ApiResult<>(500, message, null);
    }
} 