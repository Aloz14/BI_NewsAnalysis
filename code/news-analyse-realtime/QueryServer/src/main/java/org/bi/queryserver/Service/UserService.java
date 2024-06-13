package org.bi.queryserver.Service;

import org.bi.queryserver.DAO.HBaseDAO;
import org.bi.queryserver.DAO.MySQLDAO;
import org.bi.queryserver.DAO.RedisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    HBaseDAO hBaseDAO;

    @Autowired
    RedisDAO redisDAO;

    @Autowired
    MySQLDAO mySQLDAO;


}
