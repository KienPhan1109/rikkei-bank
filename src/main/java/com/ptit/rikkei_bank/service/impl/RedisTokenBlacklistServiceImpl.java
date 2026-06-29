package com.ptit.rikkei_bank.service.impl;

import com.ptit.rikkei_bank.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTokenBlacklistServiceImpl implements TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private final Set<String> localBlacklist = ConcurrentHashMap.newKeySet();
    private boolean redisAvailable = true;

    @Override
    public void blacklistToken(String token, long expiryDurationSeconds) {
        if (redisAvailable) {
            try {
                redisTemplate.opsForValue().set("blacklist:" + token, "true", expiryDurationSeconds, TimeUnit.SECONDS);
                log.info("[Redis] Blacklisted token successfully for {} seconds", expiryDurationSeconds);
                return;
            } catch (Exception e) {
                log.warn("[Redis] Redis is unavailable, falling back to local memory cache. Error: {}", e.getMessage());
                redisAvailable = false;
            }
        }
        localBlacklist.add(token);
        log.info("[Local] Blacklisted token in memory cache");
    }

    @Override
    public boolean isBlacklisted(String token) {
        if (redisAvailable) {
            try {
                Boolean exists = redisTemplate.hasKey("blacklist:" + token);
                return exists != null && exists;
            } catch (Exception e) {
                log.warn("[Redis] Redis is unavailable, falling back to local memory cache. Error: {}", e.getMessage());
                redisAvailable = false;
            }
        }
        return localBlacklist.contains(token);
    }
}
