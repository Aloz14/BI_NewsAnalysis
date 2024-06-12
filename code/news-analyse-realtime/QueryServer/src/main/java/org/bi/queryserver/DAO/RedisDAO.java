package org.bi.queryserver.DAO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisDAO {
    @Autowired
    private Jedis jedis;

    @Autowired
    private ObjectMapper objectMapper;

    public void set(String key, Object value) throws Exception {
        String jsonValue = objectMapper.writeValueAsString(value);
        jedis.set(key, jsonValue);
    }

    /**
     * @param key
     * @param valueType
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T get(String key, Class<T> valueType) throws Exception {
        String jsonValue = jedis.get(key);
        return objectMapper.readValue(jsonValue, valueType);
    }

    public void delete(String key) {
        jedis.del(key);
    }

    public boolean exists(String key) {
        return jedis.exists(key);
    }
}
