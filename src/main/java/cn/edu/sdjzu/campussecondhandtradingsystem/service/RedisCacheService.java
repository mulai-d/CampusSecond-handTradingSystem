package cn.edu.sdjzu.campussecondhandtradingsystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
public class RedisCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheService.class);

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    public RedisCacheService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> Optional<T> get(String key, TypeReference<T> typeReference) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (!StringUtils.hasText(json)) {
                return Optional.empty();
            }
            return Optional.ofNullable(objectMapper.readValue(json, typeReference));
        } catch (Exception ex) {
            log.warn("Redis cache read failed, key: {}", key, ex);
            return Optional.empty();
        }
    }

    public void set(String key, Object value, Duration timeout) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), timeout);
        } catch (Exception ex) {
            log.warn("Redis cache write failed, key: {}", key, ex);
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception ex) {
            log.warn("Redis cache delete failed, key: {}", key, ex);
        }
    }

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public void deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception ex) {
            log.warn("Redis cache pattern delete failed, pattern: {}", pattern, ex);
        }
    }
}
