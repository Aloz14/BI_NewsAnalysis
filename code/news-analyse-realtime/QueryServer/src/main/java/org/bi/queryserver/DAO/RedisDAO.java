package org.bi.queryserver.DAO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisDAO {
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private ObjectMapper objectMapper;

    public void set(String key, Object value) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key.getBytes(), objectMapper.writeValueAsBytes(value));
        }
    }

    /**
     * @param key
     * @param valueType
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T get(String key, Class<T> valueType) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] value = jedis.get(key.getBytes());
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, valueType);
        }
    }

    public void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key.getBytes());
        }
    }

    public boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key.getBytes());
        }
    }
}
