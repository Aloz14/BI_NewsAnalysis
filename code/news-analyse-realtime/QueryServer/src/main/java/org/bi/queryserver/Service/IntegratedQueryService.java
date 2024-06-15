package org.bi.queryserver.Service;


import org.bi.queryserver.DAO.HBaseDAO;
import org.bi.queryserver.DAO.MySQLDAO;
import org.bi.queryserver.DAO.RedisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntegratedQueryService {

    @Autowired
    HBaseDAO hbaseDAO;

    @Autowired
    MySQLDAO mysqlDAO;

    @Autowired
    RedisDAO redisDAO;

    public void integrateQuery(String[] userIDs,
                               String[] newsCategories,
                               int titleMinLen,
                               int titleMaxLen,
                               int bodyMinLen,
                               int bodyMaxLen) {

    }



}
