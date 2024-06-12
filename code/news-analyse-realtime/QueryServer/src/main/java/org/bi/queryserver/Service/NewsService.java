package org.bi.queryserver.Service;


import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.bi.queryserver.DAO.HBaseDAO;
import org.bi.queryserver.DAO.MySQLDAO;
import org.bi.queryserver.DAO.RedisDAO;
import org.bi.queryserver.Domain.NewsHistory;
import org.bi.queryserver.Domain.NewsInfo;
import org.bi.queryserver.Utils.PerformanceLogger;
import org.bi.queryserver.Utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    public NewsHistory getNewsHistory(String newsID) throws Exception {
        final String TABLE_NAME = "news_clicks";
        final String CF_NAME = "info";
        final String COL_NAME_NEWS_ID = "news_id";
        final String COL_NAME_EXPOSURETIME = "exposure_time";
        final String COL_NAME_DWELLTIME = "dwelltime";


        // 配置单列值过滤器
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(
                CF_NAME.getBytes(),             // 列族
                COL_NAME_NEWS_ID.getBytes(),    // 列名
                CompareOperator.EQUAL,
                newsID.getBytes()
        );

        // 启用过滤
        singleColumnValueFilter.setFilterIfMissing(true);

        // 启动性能记录
        PerformanceLogger logger = new PerformanceLogger();
        logger.start();
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

        final String redisKey = TABLE_NAME + ":" + newsID;
        if(redisDAO.exists(redisKey)) {
            logger.stop();
            logger.writeToMySQL(mysqlDAO);
            return redisDAO.get(redisKey, NewsHistory.class);
        }
        // 获取数据
        List<Map<String, String>> res = hbaseDAO.getData(TABLE_NAME, singleColumnValueFilter);

        // 结束查询，记录时间
        logger.stop();

        // 待返回的newsHistory
        NewsHistory newsHistory = new NewsHistory(newsID);

        // 只提取出曝光时间节点和对应曝光时长
        for (Map<String, String> row : res) {
            Instant exposureTime = TimeUtils.StringToInstant(row.get(CF_NAME + ":" + COL_NAME_EXPOSURETIME));
            Integer dwellTime = Integer.parseInt(row.get(CF_NAME + ":" + COL_NAME_DWELLTIME));
            newsHistory.addExposure(exposureTime, dwellTime);
        }


        logger.writeToMySQL(mysqlDAO);
        redisDAO.set(redisKey, newsHistory);


        return newsHistory;
    }

    public NewsInfo getNewsInfo(String newsID) throws Exception {
        final String TABLE_NAME = "news_info";
        final String CF_NAME = "info";
        final String COL_NAME_NEWS_ID = "news_id";
        final String COL_NAME_CATEGORY = "category";
        final String COL_NAME_TOPIC = "topic";
        final String COL_NAME_HEADLINE = "headline";
        final String COL_NAME_NEWSBODY = "news_body";

        // 配置单列值过滤器
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(
                CF_NAME.getBytes(),             // 列族
                COL_NAME_NEWS_ID.getBytes(),    // 列名
                CompareOperator.EQUAL,
                newsID.getBytes()
        );

        // 启用过滤
        singleColumnValueFilter.setFilterIfMissing(true);

        // 性能记录
        PerformanceLogger logger = new PerformanceLogger();
        logger.start();
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

        final String redisKey = TABLE_NAME + ":" + newsID;
        // 若redis中存在，则直接返回
        if (redisDAO.exists(redisKey)) {
            logger.stop();
            logger.writeToMySQL(mysqlDAO);
            return redisDAO.get(redisKey, NewsInfo.class);
        }

        // 获取数据
        List<Map<String, String>> res = hbaseDAO.getData(TABLE_NAME, singleColumnValueFilter);

        logger.stop();

        String category = null,
                topic = null,
                headline = null,
                newsBody = null;

        for (Map<String, String> row : res) {
            category = row.get(CF_NAME + ":" + COL_NAME_CATEGORY);
            topic = row.get(CF_NAME + ":" + COL_NAME_TOPIC);
            headline = row.get(CF_NAME + ":" + COL_NAME_HEADLINE);
            newsBody = row.get(CF_NAME + ":" + COL_NAME_NEWSBODY);
        }


        logger.writeToMySQL(mysqlDAO);
        NewsInfo newsInfo = new NewsInfo(
                newsID,
                category,
                topic,
                headline,
                newsBody
        );
        redisDAO.set(redisKey,newsInfo);
        return newsInfo;
    }

}
