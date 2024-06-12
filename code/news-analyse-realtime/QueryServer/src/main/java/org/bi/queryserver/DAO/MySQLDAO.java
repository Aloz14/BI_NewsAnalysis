package org.bi.queryserver.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MySQLDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert(String sql, Object... params) {
        jdbcTemplate.update(sql, params);
    }
}
