package com.yihu.ehr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.11.25 17:33
 */
@Configuration
public class RedisConfig {
    //@Value("${redis.pool.min-idle}")
    private int minIdle = 5;

    //@Value("${redis.pool.max-idle}")
    private int maxIdle = 10;

    //@Value("${redis.pool.max-total}")
    private int maxTotal = 15;

    //@Value("${redis.connection-factory.host-name}")
    private String hostName = "172.17.110.68";

    //@Value("${redis.connection-factory.port}")
    private int port  = 6379;

    @Bean
    JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxTotal);

        return poolConfig;
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig poolConfig) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setPoolConfig(poolConfig);
        connectionFactory.setHostName(hostName);
        connectionFactory.setPort(port);

        return connectionFactory;
    }

    @Autowired
    @Bean
    RedisTemplate<String, Serializable> redisTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Serializable.class));
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));

        return redisTemplate;
    }
}
