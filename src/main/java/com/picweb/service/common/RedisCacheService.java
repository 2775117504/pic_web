
package com.picweb.service.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务类，封装常用操作，使用 Jackson 序列化/反序列化
 */
@Service
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 存储值到 Redis（带过期时间）
     */
    public <T> void set(String key, T value, long timeout, TimeUnit unit) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, timeout, unit);
        } catch (Exception e) {
            throw new RuntimeException("序列化对象失败", e);
        }
    }

    /**
     * 存储值到 Redis（无过期时间）
     */
    public <T> void set(String key, T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json);
        } catch (Exception e) {
            throw new RuntimeException("序列化对象失败", e);
        }
    }

    /**
     * 获取缓存并反序列化为目标类
     */
    public <T> T get(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj instanceof String) {
            try {
                return objectMapper.readValue((String) obj, clazz);
            } catch (Exception e) {
                throw new RuntimeException("反序列化对象失败", e);
            }
        }
        return null;
    }

    /**
     * 获取缓存并反序列化为泛型类型（如 List<T>, Map<K,V> 等）
     */
    public <T> T getGeneric(String key, TypeReference<T> typeReference) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj instanceof String) {
            try {
                return objectMapper.readValue((String) obj, typeReference);
            } catch (Exception e) {
                throw new RuntimeException("反序列化泛型对象失败", e);
            }
        }
        return null;
    }

    /**
     * 判断某个 key 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 删除某个 key 的缓存
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置过期时间
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取所有匹配的 key（慎用，大数据量时性能较低）
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 批量删除 key（可传入多个 key）
     */
    public void deleteKeys(String... keys) {
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 获取原始 RedisTemplate（用于高级操作）
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 获取原始 ObjectMapper（用于自定义序列化逻辑）
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
