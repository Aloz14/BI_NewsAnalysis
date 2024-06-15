package org.bi.queryserver.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Value("${spring.data.redis.timeout}")
    private int redisTimeout;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128); // 最大连接数
        poolConfig.setMaxIdle(128);  // 最大空闲连接数
        poolConfig.setMinIdle(16);   // 最小空闲连接数
        poolConfig.setTestOnBorrow(true); // 在获取连接时测试连接是否可用
        poolConfig.setTestOnReturn(true); // 在返回连接时测试连接是否可用
        poolConfig.setTestWhileIdle(true); // 在空闲时测试连接是否可用
        poolConfig.setMinEvictableIdleTimeMillis(60000); // 连接最小空闲时间
        poolConfig.setTimeBetweenEvictionRunsMillis(30000); // 空闲连接检查周期
        poolConfig.setNumTestsPerEvictionRun(-1); // 每次检查的连接数

        return new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, redisPassword);
    }
}
