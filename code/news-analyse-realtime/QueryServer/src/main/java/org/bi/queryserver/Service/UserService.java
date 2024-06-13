package org.bi.queryserver.Service;

import org.bi.queryserver.DAO.HBaseDAO;
import org.bi.queryserver.DAO.MySQLDAO;
import org.bi.queryserver.DAO.RedisDAO;
import org.bi.queryserver.Domain.Favor;
import org.bi.queryserver.Utils.NewsCategories;
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
public class UserService {
    @Autowired
    HBaseDAO hbaseDAO;

    @Autowired
    RedisDAO redisDAO;

    @Autowired
    MySQLDAO mysqlDAO;

    @Autowired
    NewsService newsService;

    public List<Favor> getUserHistory(String userID,
                               String startTime,
                               String endTime) throws Exception {

        final String TABLE_NAME = "user_history";
        final String CF_NAME = "info";
        final String COL_NAME_USER_ID = "user_id";
        final String COL_NAME_NEWS_ID = "news_id";
        final String COL_NAME_EXPOSURETIME = "exposure_time";

        final String START_ROW_KEY = userID + startTime;
        final String END_ROW_KEY = userID + endTime;

        final int SEG_NUM = 20;

        // 创建性能记录器
        PerformanceLogger logger = new PerformanceLogger();

        // 创建StringBuilder对象用于记录查询内容
        StringBuilder queryInfo = new StringBuilder();
        queryInfo.append("SELECT * FROM ").append(TABLE_NAME).append(" WHERE ")
                .append(CF_NAME).append(":").append(COL_NAME_USER_ID).append(" = '").append(userID).append("';\n");

        // 将查询内容记录到查询信息中
        queryInfo.append("Query details:\n");
        queryInfo.append("  - Table: ").append(TABLE_NAME).append("\n");
        queryInfo.append("  - Column Family: ").append(CF_NAME).append("\n");
        queryInfo.append("  - Column Names: ").
                append(COL_NAME_NEWS_ID).append(", ").
                append(COL_NAME_USER_ID).append("\n");

        logger.setSqlContent(queryInfo.toString());

        logger.start();

        // 获取数据，范围查询，依据为RowKey
        List<Map<String, String>> res = hbaseDAO.getData(
                TABLE_NAME,
                START_ROW_KEY,
                END_ROW_KEY
        );

        // 结束查询，记录时间
        logger.stop();

        // 查询记录日志
        logger.writeToMySQL(mysqlDAO);


        // favor: 一个Instant对应一个类别
        List<Favor> favors = new ArrayList<Favor>();
        List<Instant> instants = TimeUtils.splitInstants(startTime,endTime,SEG_NUM);
        for (Instant instant : instants) {
            favors.add(new Favor(instant));
        }

        // 此处性能需要优化
        // 总的查询时间为139.0ms,然而响应时间为557ms，频繁的查询请求中，连接和处理占用了大部分时间
        // 优化方向：使用多线程
        for (Map<String, String> row : res) {
            String newsID = row.get(CF_NAME + ":" + COL_NAME_NEWS_ID);
            Instant exposureTime = TimeUtils.stringToInstant(row.get(CF_NAME + ":" + COL_NAME_EXPOSURETIME));
            String category = newsService.getNewsCategory(newsID);
            for (int i = 0; i < instants.size() - 1; i++) {
                Instant startInstant = instants.get(i);
                Instant endInstant = instants.get(i + 1);
                if (exposureTime.isAfter(startInstant) && exposureTime.isBefore(endInstant)) {
                    favors.get(i).addCategoryCount(category);
                    break;
                }
            }
        }

        return favors;
    }
}
