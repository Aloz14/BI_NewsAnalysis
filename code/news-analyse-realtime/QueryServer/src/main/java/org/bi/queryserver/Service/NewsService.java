package org.bi.queryserver.Service;


import org.bi.queryserver.DAO.HBaseDAO;
import org.bi.queryserver.DAO.MySQLDAO;
import org.bi.queryserver.DAO.RedisDAO;
import org.bi.queryserver.Domain.Clicks;
import org.bi.queryserver.Domain.NewsInfo;
import org.bi.queryserver.Utils.PerformanceLogger;
import org.bi.queryserver.Utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsService {

    @Autowired
    HBaseDAO hbaseDAO;

    @Autowired
    MySQLDAO mysqlDAO;

    @Autowired
    RedisDAO redisDAO;

    /**
     * [Archived]
     * 单个新闻生命周期的查询
     * @param newsID
     * @return List
     * @throws Exception
     */
    public List<Clicks> getNewsHistory(String newsID,
                                       String startTime,
                                       String endTime) throws Exception {

        // 加入空值处理

        final String TABLE_NAME = "news_clicks";
        final String CF_NAME = "info";
        final String COL_NAME_NEWS_ID = "news_id";
        final String COL_NAME_EXPOSURETIME = "exposure_time";
        final String COL_NAME_DWELLTIME = "dwelltime";

        final String START_ROW_KEY = newsID + startTime;
        final String END_ROW_KEY = newsID + endTime;

        final int SEG_NUM = 20;


        /*
        // 配置单列值过滤器
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(
                CF_NAME.getBytes(),             // 列族
                COL_NAME_NEWS_ID.getBytes(),    // 列名
                CompareOperator.EQUAL,
                newsID.getBytes()
        );

        // 启用过滤
        singleColumnValueFilter.setFilterIfMissing(true);

         */

        // 创建性能记录器
        PerformanceLogger logger = new PerformanceLogger();

        // 创建StringBuilder对象用于记录查询内容
        StringBuilder queryInfo = new StringBuilder();
        queryInfo.append("SELECT * FROM ").append(TABLE_NAME).append(" WHERE ")
                .append(CF_NAME).append(":").append(COL_NAME_NEWS_ID).append(" = '").append(newsID).append("';\n");

        // 将查询内容记录到查询信息中
        queryInfo.append("Query details:\n");
        queryInfo.append("  - Table: ").append(TABLE_NAME).append("\n");
        queryInfo.append("  - Column Family: ").append(CF_NAME).append("\n");
        queryInfo.append("  - Column Names: ").
                append(COL_NAME_EXPOSURETIME).append(", ").
                append(COL_NAME_DWELLTIME).append("\n");

        logger.setSqlContent(queryInfo.toString());

        logger.start();

        /* Redis TBD


        final String redisKey = TABLE_NAME + ":" + newsID;
        if(redisDAO.exists(redisKey)) {
            logger.stop();
            logger.writeToMySQL(mysqlDAO);
            return redisDAO.get(redisKey, NewsHistory.class);
        }
        */

        // 获取数据，范围查询，依据为RowKey
        List<Map<String, String>> res = hbaseDAO.getData(
                TABLE_NAME,
                START_ROW_KEY,
                END_ROW_KEY
        );

        // 结束查询，记录时间
        logger.stop();


        // 时间节点和对应计数器
        // clicks: 最终返回的结果，为时间戳以及对应的点击量
        List<Clicks> clicks = new ArrayList<>();
        // instants： 存储切分后的时间节点
        List<Instant> instants = TimeUtils.splitInstants(startTime, endTime, SEG_NUM);
        // clickCounts： Map, 计数器
        Map<Instant, Integer> clickCounts = new HashMap<>();
        for (Instant instant : instants) {
            clickCounts.put(instant, 0);
        }

        // 只提取出曝光时间节点和对应曝光时长
        for (Map<String, String> row : res) {
            Instant exposureTime = TimeUtils.stringToInstant(row.get(CF_NAME + ":" + COL_NAME_EXPOSURETIME));

            for (int i = 0; i < instants.size() - 1; i++) {
                Instant startInstant = instants.get(i);
                Instant endInstant = instants.get(i + 1);
                if (exposureTime.isAfter(startInstant) && exposureTime.isBefore(endInstant)) {
                    clickCounts.put(startInstant, clickCounts.get(startInstant) + 1);
                    break; // No need to check further
                }
            }
        }


        // 存储到对象当中
        for (Instant instant : instants) {
            clicks.add(
                    new Clicks(instant, clickCounts.get(instant))
            );
        }

        // 查询记录日志
        logger.writeToMySQL(mysqlDAO);


        /* Redis TBD

        redisDAO.set(redisKey, newsHistory);
         */

        return clicks;
    }

    /**
     * [Archived]
     * 获取新闻信息的接口
     * @param newsID
     * @return NewsInfo
     * @throws Exception
     */
    public NewsInfo getNewsInfo(String newsID) throws Exception {
        if (newsID == null)
            return null;

        final String TABLE_NAME = "news_info";
        final String CF_NAME = "info";
        final String COL_NAME_NEWS_ID = "news_id";
        final String COL_NAME_CATEGORY = "category";
        final String COL_NAME_TOPIC = "topic";
        final String COL_NAME_HEADLINE = "headline";
        final String COL_NAME_NEWSBODY = "news_body";

        // 性能记录
        PerformanceLogger logger = new PerformanceLogger();
        StringBuilder queryInfo = new StringBuilder();
        queryInfo.append("SELECT * FROM ").append(TABLE_NAME).append(" WHERE ")
                .append(COL_NAME_NEWS_ID).append(" = '").append(newsID).append("';\n");
        queryInfo.append("Query details:\n");
        queryInfo.append("  - Table: ").append(TABLE_NAME).append("\n");
        queryInfo.append("  - Column Family: ").append(CF_NAME).append("\n");
        queryInfo.append("  - Column Name: ").
                append(COL_NAME_NEWS_ID).append(",").
                append(COL_NAME_CATEGORY).append(",").
                append(COL_NAME_TOPIC).append(",").
                append(COL_NAME_HEADLINE).append(",").
                append(COL_NAME_NEWSBODY).append("\n");

        logger.setSqlContent(queryInfo.toString());
        logger.start();

        /* 无需再使用Redis优化


        final String redisKey = TABLE_NAME + ":" + newsID;
        // 若redis中存在，则直接返回
        if (redisDAO.exists(redisKey)) {
            logger.stop();
            logger.writeToMySQL(mysqlDAO);
            return redisDAO.get(redisKey, NewsInfo.class);
        }*/

        // 获取数据
        Map<String, String> res = hbaseDAO.getData(TABLE_NAME, newsID);

        logger.stop();

        String category = null,
                topic = null,
                headline = null,
                newsBody = null;

        category = res.get(CF_NAME + ":" + COL_NAME_CATEGORY);
        topic = res.get(CF_NAME + ":" + COL_NAME_TOPIC);
        headline = res.get(CF_NAME + ":" + COL_NAME_HEADLINE);
        newsBody = res.get(CF_NAME + ":" + COL_NAME_NEWSBODY);


        logger.writeToMySQL(mysqlDAO);

        NewsInfo newsInfo = new NewsInfo(
                newsID,
                category,
                topic,
                headline,
                newsBody
        );

        return newsInfo;
    }


    /**
     * 轻量地获取新闻所属的种类
     *
     * @param newsID
     * @return String
     * @throws Exception
     */
    public String getNewsCategory(String newsID) throws Exception {

        if(newsID == null)
            return null;

        final String TABLE_NAME = "news_info";
        final String CF_NAME = "info";
        final String COL_NAME_NEWS_ID = "news_id";
        final String COL_NAME_CATEGORY = "category";

        final String redisKey = newsID + ":" + COL_NAME_CATEGORY;
        return redisDAO.get(redisKey, String.class);


        /*
        // 获取数据
        Map<String, String> res = hbaseDAO.getData(TABLE_NAME, newsID);
        String category = res.get(CF_NAME + ":" + COL_NAME_CATEGORY);
        redisDAO.set(redisKey, category);

        return category;*/
    }

}
