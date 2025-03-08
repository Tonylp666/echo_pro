package com.echo.blog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT令牌提供者
 */
@Slf4j
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret:defaultSecretKeyThatShouldBeChangedInProduction}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}")
    private long validityInMilliseconds; // 默认1天
    
    private Key key;
    
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    
    /**
     * 创建令牌
     *
     * @param username 用户名
     * @param roles 角色
     * @return 令牌
     */
    public String createToken(String username, Collection<? extends GrantedAuthority> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 获取认证信息
     *
     * @param token 令牌
     * @return 认证信息
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        String username = claims.getSubject();
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("roles", String[].class))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        UserDetails userDetails = new User(username, "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
    
    /**
     * 从请求中解析令牌
     *
     * @param req 请求
     * @return 令牌
     */
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * 验证令牌
     *
     * @param token 令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}