package org.bi.queryserver.Service.impl;


import org.bi.queryserver.DAO.HBaseDAO;
import org.bi.queryserver.DAO.MySQLDAO;
import org.bi.queryserver.DAO.RedisDAO;
import org.bi.queryserver.Domain.Clicks;
import org.bi.queryserver.Domain.NewsInfo;
import org.bi.queryserver.Service.IIntegratedQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class IntegratedQueryService implements IIntegratedQueryService {

    @Autowired
    HBaseDAO hbaseDAO;

    @Autowired
    MySQLDAO mysqlDAO;

    @Autowired
    RedisDAO redisDAO;

    @Autowired
    NewsService newsService;


    /**
     * @param userIDs
     * @param newsCategories
     * @param titleMinLen    = 0
     * @param titleMaxLen
     * @param bodyMinLen     = 0
     * @param bodyMaxLen
     */
    public List<Clicks> integratedQuery(String[] userIDs,
                                        String[] newsCategories,
                                        String startTime,
                                        String endTime,
                                        int titleMinLen,
                                        int titleMaxLen,
                                        int bodyMinLen,
                                        int bodyMaxLen) throws Exception {

        // 按理来说应该是一开始有所有的News ID,然后慢慢筛选，再统计点击量
        // 但这样的响应时间可能会有些过于大了，因此限制必须要选择


        // 通过用户ID获取的点击过的新闻ID集合
        Set<String> userIDFilterSet = new HashSet<String>();

        for (String userID : userIDs) {
            List<String> newsIDs = newsService.getClickedNewsIDsByUserID(
                    userID,
                    startTime,
                    endTime);

            for (String newsID : newsIDs) {
                userIDFilterSet.add(newsID);
            }
        }

        // 通过种类获取的点击过的新闻ID集合
        Set<String> categoryFilterSet = new HashSet<>();
        for (String category : newsCategories) {
            List<String> newsIDs = newsService.getClickedNewsIDsByCategory(
                    category,
                    startTime,
                    endTime
            );

            for (String newsID : newsIDs) {
                categoryFilterSet.add(newsID);
            }
        }

        // 目标新闻ID集合
        Set<String> newsIDSet = null;

        if (userIDFilterSet.isEmpty() && categoryFilterSet.isEmpty()) {
            // 所有新闻点击记录，过于庞大，暂且不考虑
        } else if (categoryFilterSet.isEmpty()) {
            newsIDSet = userIDFilterSet;
        } else if (userIDFilterSet.isEmpty()) {
            newsIDSet = categoryFilterSet;
        } else {
            newsIDSet = categoryFilterSet;
            newsIDSet.retainAll(userIDFilterSet);
        }


        // 通过标题长度和内容长度进行筛选
        for (String newsID : newsIDSet) {
            NewsInfo newsInfo = newsService.getNewsInfo(newsID);
            if (newsInfo != null) {
                if (newsInfo.getHeadline().length()>titleMaxLen){
                    newsIDSet.remove(newsID);
                    continue;
                }

                if (newsInfo.getNewsBody().length()>bodyMaxLen){
                    newsIDSet.remove(newsID);
                }
            }
        }


        List<Clicks> res = null;
        // 统计剩下新闻的点击量
        for (String newsID : newsIDSet) {
            List<Clicks> newsClicks = newsService.getClicksHistory(
                    newsID,
                    startTime,
                    endTime
            );

            if (res == null) {
                res = newsClicks;
            }
            else{
                for (int i = 0; i < res.size(); i++) {
                    res.get(i).setClicks(
                            res.get(i).getClicks() + newsClicks.get(i).getClicks()
                    );
                }
            }

        }


        return res;
    }


}
