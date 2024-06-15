package org.bi.queryserver.Service.impl;


import org.bi.queryserver.DAO.HBaseDAO;
import org.bi.queryserver.DAO.MySQLDAO;
import org.bi.queryserver.DAO.RedisDAO;
import org.bi.queryserver.Domain.Clicks;
import org.bi.queryserver.Service.IIntegratedQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntegratedQueryService implements IIntegratedQueryService {

    @Autowired
    HBaseDAO hbaseDAO;

    @Autowired
    MySQLDAO mysqlDAO;

    @Autowired
    RedisDAO redisDAO;


    /**
     *
     * @param userIDs
     * @param newsCategories
     * @param titleMinLen = 0
     * @param titleMaxLen
     * @param bodyMinLen = 0
     * @param bodyMaxLen
     */
    public List<Clicks> integratedQuery(String[] userIDs,
                                       String[] newsCategories,
                                       String startTime,
                                       String endTime,
                                       int titleMinLen,
                                       int titleMaxLen,
                                       int bodyMinLen,
                                       int bodyMaxLen) {

        // 无用户筛选
        if (userIDs == null || userIDs.length == 0) {

        }

        // 无种类筛选
        if(newsCategories == null || newsCategories.length == 0) {

        }

        // 所有种类、所有用户




    }



}
