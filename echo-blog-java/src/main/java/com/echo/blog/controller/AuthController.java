package com.echo.blog.controller;

import com.echo.blog.common.api.ApiResult;
import com.echo.blog.model.dto.LoginDTO;
import com.echo.blog.model.dto.RegisterDTO;
import com.echo.blog.model.vo.TokenVO;
import com.echo.blog.model.vo.UserVO;
import com.echo.blog.security.JwtTokenProvider;
import com.echo.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证", description = "认证相关接口")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    
    /**
     * 登录
     *
     * @param loginDTO 登录信息
     * @return 令牌
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public ApiResult<TokenVO> login(@RequestBody @Valid LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.createToken(authentication.getName(), authentication.getAuthorities());
        
        return ApiResult.success(new TokenVO(token));
    }
    
    /**
     * 注册
     *
     * @param registerDTO 注册信息
     * @return 用户信息
     */
    @PostMapping("/register")
    @Operation(summary = "注册")
    public ApiResult<UserVO> register(@RequestBody @Valid RegisterDTO registerDTO) {
        UserVO userVO = userService.register(registerDTO);
        return ApiResult.success(userVO);
    }
}
