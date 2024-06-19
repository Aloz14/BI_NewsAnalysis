package org.bi.queryserver.DAO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisDAO {
    // Redis连接池
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

    /**
     * 一次性获取多个 key 对应的 value
     * @param keys 要获取的 key 列表
     * @return 对应的 value 列表
     */
    public List<String> mget(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.mget(keys);
        }
    }

    public <T> List<T> mget(Class<T> valueType,String... keys) throws JsonProcessingException {
        if(keys.equals("")||keys.length==0)
            return null;
        List<String> values = mget(keys);
        List<T> result = new ArrayList<>();
        for (String value : values) {
            result.add(objectMapper.readValue(value, valueType));
        }
        return result;
    }

}
