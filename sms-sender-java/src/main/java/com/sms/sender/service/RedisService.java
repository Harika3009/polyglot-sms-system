package com.sms.sender.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isBlocked(String userId) {
        Boolean exists = redisTemplate.opsForSet().isMember("blocked-users", userId);
        return Boolean.TRUE.equals(exists);
    }
}
