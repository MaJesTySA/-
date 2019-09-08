package com.inside.utils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisOperator {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    public void expire(String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    public long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public void del(String key) {
        redisTemplate.delete(key);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public String hget(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    public void hdel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    public Map<Object, Object> hgetall(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public long lpush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    public String lpop(String key) {
        return (String) redisTemplate.opsForList().leftPop(key);
    }

    public long rpush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

}