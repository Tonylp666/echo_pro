package com.echo.blog.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    
    public RedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
    
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
    
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
}