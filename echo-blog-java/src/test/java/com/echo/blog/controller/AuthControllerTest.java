package com.echo.blog.controller;

import com.echo.blog.model.dto.LoginDTO;
import com.echo.blog.model.dto.RegisterDTO;
import com.echo.blog.model.vo.TokenVO;
import com.echo.blog.model.vo.UserVO;
import com.echo.blog.security.JwtTokenProvider;
import com.echo.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AuthenticationManager authenticationManager;
    
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    
    @MockBean
    private UserService userService;
    
    @Test
    public void testLogin() throws Exception {
        // 准备测试数据
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password");
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.createToken(any(), any())).thenReturn("test-token");
        
        // 执行测试
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("test-token"));
    }
    
    @Test
    public void testRegister() throws Exception {
        // 准备测试数据
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("password");
        registerDTO.setEmail("newuser@example.com");
        
        UserVO userVO = new UserVO();
        userVO.setId(1L);
        userVO.setUsername("newuser");
        userVO.setEmail("newuser@example.com");
        userVO.setRoles(Collections.singletonList("USER"));
        
        when(userService.register(any())).thenReturn(userVO);
        
        // 执行测试
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("newuser@example.com"));
    }
} 