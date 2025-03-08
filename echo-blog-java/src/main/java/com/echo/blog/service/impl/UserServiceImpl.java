package com.echo.blog.service.impl;

import com.echo.blog.entity.Role;
import com.echo.blog.entity.User;
import com.echo.blog.entity.UserRole;
import com.echo.blog.model.dto.RegisterDTO;
import com.echo.blog.model.vo.UserVO;
import com.echo.blog.repository.RoleRepository;
import com.echo.blog.repository.UserRepository;
import com.echo.blog.repository.UserRoleRepository;
import com.echo.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional
    public UserVO register(RegisterDTO registerDTO) {
        // 检查用户名是否存在
        if (existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setStatus("ACTIVE");
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        // 分配角色
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            throw new RuntimeException("角色不存在");
        }
        
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setUserId(savedUser.getId());
        userRoleEntity.setRoleId(userRole.getId());
        userRoleEntity.setCreatedTime(LocalDateTime.now());
        userRoleRepository.save(userRoleEntity);
        
        // 返回用户信息
        return convertToVO(savedUser, Collections.singletonList(userRole));
    }
    
    @Override
    public UserVO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        String username = authentication.getName();
        User user = findByUsername(username);
        if (user == null) {
            return null;
        }
        
        List<Role> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(userRole -> roleRepository.findById(userRole.getRoleId()).orElse(null))
                .collect(Collectors.toList());
        
        return convertToVO(user, roles);
    }
    
    /**
     * 转换为VO
     *
     * @param user 用户
     * @param roles 角色列表
     * @return 用户VO
     */
    private UserVO convertToVO(User user, List<Role> roles) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        List<String> roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        userVO.setRoles(roleNames);
        
        return userVO;
    }
}