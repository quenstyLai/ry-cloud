package com.ruoyi.common.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * spring redis 工具类
 * 
 * @author ruoyi
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
public class ReactiveRedisService
{
    @Autowired
    public ReactiveRedisTemplate reactiveRedisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value)
    {
        reactiveRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit)
    {
        reactiveRedisTemplate.opsForValue().set(key, value, Duration.of(timeout, timeUnit.toChronoUnit()));
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public Mono<Boolean> expire(final String key, final long timeout)
    {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public Mono<Boolean> expire(final String key, final long timeout, final TimeUnit unit)
    {
        return reactiveRedisTemplate.expire(key, Duration.of(timeout, unit.toChronoUnit()));
    }

    /**
     * 获取有效时间
     *
     * @param key Redis键
     * @return 有效时间
     */
    public Mono<Duration> getExpire(final String key)
    {
        return reactiveRedisTemplate.getExpire(key);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Mono<Boolean> hasKey(String key)
    {
        return reactiveRedisTemplate.hasKey(key);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> Mono<T> getCacheObject(final String key)
    {
        ReactiveValueOperations operation = reactiveRedisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public Mono<Boolean> deleteObject(final String key)
    {
        Mono<Long> delete = reactiveRedisTemplate.delete(key);
        return delete.map(count -> count > 0);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public Mono<Boolean> deleteObject(final Collection collection)
    {
        Mono<Long> delete = reactiveRedisTemplate.delete(collection);

        return delete.map(count -> count > 0);
    }

    /**
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> Mono<Long> setCacheList(final String key, final List<T> dataList)
    {
        return reactiveRedisTemplate.opsForList().rightPushAll(key, dataList);
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> Flux<T> getCacheList(final String key)
    {
        return reactiveRedisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> Mono<Long> setCacheSet(final String key, final Set<T> dataSet)
    {
        return reactiveRedisTemplate.opsForSet()
                .add(key,dataSet);
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Flux<T> getCacheSet(final String key)
    {
        return reactiveRedisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> Mono<Void> setCacheMap(final String key, final Map<String, T> dataMap)
    {
        if (dataMap != null) {
            return reactiveRedisTemplate.opsForHash().putAll(key, dataMap);
        }
        return Mono.empty();
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Flux<T> getCacheMap(final String key)
    {
        return reactiveRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @param value 值
     */
    public <T> Mono<Void> setCacheMapValue(final String key, final String hKey, final T value)
    {
        return reactiveRedisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> Mono<T> getCacheMapValue(final String key, final String hKey)
    {
        return reactiveRedisTemplate.opsForHash().get(key,hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> Mono<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys)
    {
        return reactiveRedisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 删除Hash中的某条数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return 是否成功
     */
    public Mono<Boolean> deleteCacheMapValue(final String key, final String hKey)
    {
        ReactiveHashOperations<String, String, Object> operations = reactiveRedisTemplate.opsForHash();
        return  operations
                .remove(key, hKey)
                .map(count -> count > 0);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Flux<String> keys(final String pattern)
    {
        return reactiveRedisTemplate.keys(pattern);
    }
}
