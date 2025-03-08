package com.echo.blog.exception;

public class PlatformPublishException extends RuntimeException {
    public PlatformPublishException(String message) {
        super(message);
    }

    public PlatformPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}