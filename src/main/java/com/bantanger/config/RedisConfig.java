package com.bantanger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/22 9:22
 */

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置 template 的连接工厂
        template.setConnectionFactory(factory);

        // 设置 key 的序列化方式
        template.setKeySerializer(RedisSerializer.string());

        // 设置 value 的序列化方式
        // value 的 类型是各种各样的数据结构，将其转换成json格式的字节较好
        template.setValueSerializer(RedisSerializer.json());

        // 设置 hash 的 key 的 序列化方式
        template.setHashKeySerializer(RedisSerializer.string());

        // 设置 hash 的 value 的 序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet(); // 让序列化生效
        return template;
    }

}
