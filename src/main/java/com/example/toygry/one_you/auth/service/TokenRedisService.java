package com.example.toygry.one_you.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * Redis를 사용한 JWT 토큰 관리 서비스
 * 토큰 저장, 조회, 삭제, 유효성 검증 기능 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    
    // Redis 키 패턴: "jwt:token:{userId}"
    private static final String TOKEN_KEY_PREFIX = "jwt:token:";

    /**
     * 사용자 토큰을 Redis에 저장
     * @param userId 사용자 ID
     * @param token JWT 토큰
     * @param ttlSeconds TTL (초)
     */
    public void saveToken(UUID userId, String token, long ttlSeconds) {
        String key = TOKEN_KEY_PREFIX + userId.toString();
        redisTemplate.opsForValue().set(key, token, Duration.ofSeconds(ttlSeconds));
        log.info("Token saved for user: {}, TTL: {}s", userId, ttlSeconds);
    }

    /**
     * 사용자의 저장된 토큰 조회
     * @param userId 사용자 ID
     * @return 저장된 토큰 (없으면 null)
     */
    public String getToken(UUID userId) {
        String key = TOKEN_KEY_PREFIX + userId.toString();
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 토큰이 Redis에 저장된 토큰과 일치하는지 확인
     * @param userId 사용자 ID
     * @param token 검증할 토큰
     * @return 유효하면 true
     */
    public boolean isTokenValid(UUID userId, String token) {
        String storedToken = getToken(userId);
        boolean isValid = storedToken != null && storedToken.equals(token);
        log.debug("Token validation for user {}: {}", userId, isValid);
        return isValid;
    }

    /**
     * 사용자의 토큰 삭제 (로그아웃)
     * @param userId 사용자 ID
     */
    public void deleteToken(UUID userId) {
        String key = TOKEN_KEY_PREFIX + userId.toString();
        Boolean deleted = redisTemplate.delete(key);
        log.info("Token deleted for user: {}, result: {}", userId, deleted);
    }

    /**
     * 토큰의 남은 만료시간 조회 (초)
     * @param userId 사용자 ID
     * @return 남은 시간 (초), 키가 없으면 -1
     */
    public long getTokenTtl(UUID userId) {
        String key = TOKEN_KEY_PREFIX + userId.toString();
        Long ttl = redisTemplate.getExpire(key);
        return ttl != null ? ttl : -1;
    }

    /**
     * 토큰 TTL 갱신
     * @param userId 사용자 ID
     * @param ttlSeconds 새로운 TTL (초)
     */
    public void refreshTokenTtl(UUID userId, long ttlSeconds) {
        String key = TOKEN_KEY_PREFIX + userId.toString();
        redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
        log.info("Token TTL refreshed for user: {}, new TTL: {}s", userId, ttlSeconds);
    }
}