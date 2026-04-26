package com.ruoyi.common.redis.configure;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置
 * 
 * @author ruoyi
 */
@SuppressWarnings("deprecation")
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport
{
    @Bean(name = "ruoyiRedisTemplate")
    @Primary
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public RedisTemplate<Object, Object> ruoyiRedisTemplate(RedisConnectionFactory connectionFactory)
    {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setValueSerializer(serializer);

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(StringRedisSerializer.UTF_8);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    //配置序列化器
    @Bean
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public RedisSerializationContext redisSerializationContext() {
        FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class);
        RedisSerializationContext.RedisSerializationContextBuilder builder = RedisSerializationContext.newSerializationContext();
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        builder.key(StringRedisSerializer.UTF_8);
        builder.value(serializer);
        // Hash的key也采用StringRedisSerializer的序列化方式
        builder.hashKey(StringRedisSerializer.UTF_8);
        builder.hashValue(serializer);

        return builder.build();
    }

    @Bean(name = "ruoyiReactiveRedisTemplate")
    @Primary
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory,RedisSerializationContext serializationContext ) {
        return new ReactiveRedisTemplate<>(connectionFactory,serializationContext);
    }
}
