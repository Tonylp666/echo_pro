package com.echo.blog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Data;
import java.time.LocalDateTime;
import com.echo.blog.enums.PlatformType;

@Data
@Entity
@Table(name = "a_user_platform_auth")
public class UserPlatformAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    private PlatformType platformType;
    
    private String authToken;
    
    private LocalDateTime expireTime;
    
    private String refreshToken;
    
    private String platformUserId;
    
    private String platformUserName;
    
    private String status;
    
    private LocalDateTime createdTime;
    
    private LocalDateTime updatedTime;
}