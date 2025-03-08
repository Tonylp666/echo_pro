package com.echo.blog.service.impl;

import com.echo.blog.entity.UserPlatformAuth;
import com.echo.blog.enums.PlatformType;
import com.echo.blog.model.dto.PlatformAuthDTO;
import com.echo.blog.model.vo.UserPlatformAuthVO;
import com.echo.blog.repository.UserPlatformAuthRepository;
import com.echo.blog.service.UserPlatformAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户平台授权服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPlatformAuthServiceImpl implements UserPlatformAuthService {
    
    private final UserPlatformAuthRepository userPlatformAuthRepository;
    
    @Override
    public List<UserPlatformAuthVO> getUserPlatformAuths(Long userId) {
        List<UserPlatformAuth> auths = userPlatformAuthRepository.findByUserId(userId);
        return auths.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public UserPlatformAuthVO addPlatformAuth(Long userId, PlatformAuthDTO authDTO) {
        // 检查是否已存在该平台的授权
        UserPlatformAuth existingAuth = userPlatformAuthRepository.findByUserIdAndPlatformType(
                userId, authDTO.getPlatformType());
        
        if (existingAuth != null) {
            // 更新现有授权
            BeanUtils.copyProperties(authDTO, existingAuth);
            existingAuth.setUpdatedTime(LocalDateTime.now());
            existingAuth.setStatus("ACTIVE");
            
            UserPlatformAuth savedAuth = userPlatformAuthRepository.save(existingAuth);
            return convertToVO(savedAuth);
        } else {
            // 创建新授权
            UserPlatformAuth auth = new UserPlatformAuth();
            BeanUtils.copyProperties(authDTO, auth);
            auth.setUserId(userId);
            auth.setStatus("ACTIVE");
            auth.setCreatedTime(LocalDateTime.now());
            auth.setUpdatedTime(LocalDateTime.now());
            
            UserPlatformAuth savedAuth = userPlatformAuthRepository.save(auth);
            return convertToVO(savedAuth);
        }
    }
    
    @Override
    @Transactional
    public UserPlatformAuthVO updatePlatformAuth(Long authId, PlatformAuthDTO authDTO) {
        UserPlatformAuth auth = userPlatformAuthRepository.findById(authId)
                .orElseThrow(() -> new RuntimeException("授权信息不存在"));
        
        BeanUtils.copyProperties(authDTO, auth);
        auth.setUpdatedTime(LocalDateTime.now());
        
        UserPlatformAuth savedAuth = userPlatformAuthRepository.save(auth);
        return convertToVO(savedAuth);
    }
    
    @Override
    @Transactional
    public boolean deletePlatformAuth(Long authId) {
        try {
            userPlatformAuthRepository.deleteById(authId);
            return true;
        } catch (Exception e) {
            log.error("删除平台授权失败", e);
            return false;
        }
    }
    
    @Override
    public UserPlatformAuth getUserPlatformAuth(Long userId, PlatformType platformType) {
        return userPlatformAuthRepository.findByUserIdAndPlatformType(userId, platformType);
    }
    
    @Override
    public UserPlatformAuth getUserPlatformAuth(Long userId, String platformType) {
        return userPlatformAuthRepository.findByUserIdAndPlatformTypeString(userId, platformType);
    }
    
    /**
     * 转换为VO
     *
     * @param auth 授权实体
     * @return 授权VO
     */
    private UserPlatformAuthVO convertToVO(UserPlatformAuth auth) {
        UserPlatformAuthVO vo = new UserPlatformAuthVO();
        BeanUtils.copyProperties(auth, vo);
        return vo;
    }
} 