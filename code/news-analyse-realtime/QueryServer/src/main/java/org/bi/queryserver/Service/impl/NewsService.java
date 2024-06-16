package org.bi.queryserver.Service.impl;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.spark.JavaHBaseContext;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.bi.queryserver.DAO.HBaseDAO;
import org.bi.queryserver.DAO.MySQLDAO;
import org.bi.queryserver.DAO.RedisDAO;
import org.bi.queryserver.Domain.Clicks;
import org.bi.queryserver.Domain.NewsInfo;
import org.bi.queryserver.Service.INewsService;
import org.bi.queryserver.Utils.PerformanceLogger;
import org.bi.queryserver.Utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class NewsService implements INewsService {

    @Autowired
    HBaseDAO hbaseDAO;

    @Autowired
    MySQLDAO mysqlDAO;

    @Autowired
    RedisDAO redisDAO;

    @Autowired
    JavaSparkContext jsc;

    public void testSpark() {
        // 设置 Spark 配置
        SparkConf sparkConf = new SparkConf().setAppName("Spark Streaming HBase Example").setMaster("local[*]");
        // 创建 Spark Streaming 上下文
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, new Duration(5000)); // 设置批处理时间间隔为 5 秒

        // 配置 HBase
        Configuration hbaseConf = HBaseConfiguration.create();
        hbaseConf.set("hbase.zookeeper.quorum", "122.51.75.129");
        hbaseConf.set("hbase.zookeeper.property.clientPort", "2181");

        // 初始化 HBase 上下文
        JavaSparkContext sparkContext = streamingContext.sparkContext();
        JavaHBaseContext hbaseContext = new JavaHBaseContext(sparkContext, hbaseConf);

        // 创建一个队列用于存放数据
        Queue<JavaRDD<Integer>> queue = new LinkedBlockingQueue<>();

        // 创建 JavaDStream
        JavaDStream<Integer> javaDStream = streamingContext.queueStream(queue);

        // 向队列中添加数据
        List<Integer> data = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            data.add(i);
        }
        queue.add(streamingContext.sparkContext().parallelize(data));

        // 处理流式数据
        javaDStream.foreachRDD(rdd -> {
            if (!rdd.isEmpty()) {
                // 对 RDD 中的数据进行处理，例如输出每条数据的内容
                rdd.foreach(dataPoint -> {
                    // 处理每条数据
                    System.out.println("Received data: " + dataPoint);
                });
            }
        });


        // 启动 Spark Streaming
        streamingContext.start();
    }



    /**
     * 单个新闻生命周期的查询，Archived
     *
     * @param newsID
     * @return List
     * @throws Exception
     */
    @Override
    public List<Clicks> getClicksHistory(String newsID,
                                         String startTime,
                                         String endTime) throws Exception {

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

        /* Redis

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


        // 结束查询，记录时间
        logger.stop();

        // 记录查询日志
        logger.writeToMySQL(mysqlDAO);


        /* Redis

        redisDAO.set(redisKey, newsHistory);
         */

        return clicks;
    }


    /**
     * 获取新闻信息的接口，Archived
     *
     * @param newsID
     * @return NewsInfo
     * @throws Exception
     */
    @Override
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

        final String redisKey = TABLE_NAME + ":" + newsID;
        // 若redis中存在，则直接返回
        if (redisDAO.exists(redisKey)) {
            logger.stop();
            logger.writeToMySQL(mysqlDAO);
            return redisDAO.get(redisKey, NewsInfo.class);
        }

        // 获取数据
        Map<String, String> res = hbaseDAO.getData(TABLE_NAME, newsID);

        String category = res.get(CF_NAME + ":" + COL_NAME_CATEGORY),
                topic = res.get(CF_NAME + ":" + COL_NAME_TOPIC),
                headline = res.get(CF_NAME + ":" + COL_NAME_HEADLINE),
                newsBody = res.get(CF_NAME + ":" + COL_NAME_NEWSBODY);

        logger.stop();
        logger.writeToMySQL(mysqlDAO);

        NewsInfo newsInfo = new NewsInfo(
                newsID,
                category,
                topic,
                headline.length(),
                newsBody.length()
        );

        redisDAO.set(redisKey, newsInfo);

        return newsInfo;
    }

    @Override
    public List<NewsInfo> getNewsInfo(String[] newsIDs) throws Exception{
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
                .append(COL_NAME_NEWS_ID).append(" = '").append("newsIDs").append("';\n");
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

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        String[] redisKeys = new String[newsIDs.length];
        int index = 0;
        for (String newsID:newsIDs){
            redisKeys[index++] = TABLE_NAME + ":" + newsID;
        }




        for (String newsID : newsIDs) {
            final String redisKey = TABLE_NAME + ":" + newsID;
            executor.submit(() -> {
                if (!redisDAO.exists(redisKey)) {
                    // 获取数据
                    Map<String, String> res = hbaseDAO.getData(TABLE_NAME, newsID);

                    String category = res.get(CF_NAME + ":" + COL_NAME_CATEGORY),
                            topic = res.get(CF_NAME + ":" + COL_NAME_TOPIC),
                            headline = res.get(CF_NAME + ":" + COL_NAME_HEADLINE),
                            newsBody = res.get(CF_NAME + ":" + COL_NAME_NEWSBODY);

                    NewsInfo newsInfo = new NewsInfo(
                            newsID,
                            category,
                            topic,
                            headline.length(),
                            newsBody.length()
                    );

                    try {
                        redisDAO.set(redisKey, newsInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        List<NewsInfo> newsInfos = redisDAO.mget(NewsInfo.class,redisKeys);

        logger.stop();
        logger.writeToMySQL(mysqlDAO);
        return newsInfos;
    }

    @Override
    public List<NewsInfo> getNewsInfo(List<String> newsIDs) throws Exception {
        String[] newsIDArray = new String[newsIDs.size()];
        int index = 0;
        for (String newsID : newsIDs) {
            newsIDArray[index++] = newsID;
        }
        return getNewsInfo(newsIDArray);
    }

    /**
     * 根据用户ID获取用户访问过的新闻ID列表
     *
     * @param userID
     * @param startTime
     * @param endTime
     * @return 新闻ID列表
     * @throws Exception
     */
    @Override
    public List<String> getClickedNewsIDsByUserID(String userID,
                                                  String startTime,
                                                  String endTime) throws Exception {
        final String TABLE_NAME = "user_history";
        final String CF_NAME = "info";
        final String COL_NAME_USER_ID = "user_id";
        final String COL_NAME_NEWS_ID = "news_id";
        final String COL_NAME_EXPOSURETIME = "exposure_time";

        final String START_ROW_KEY = userID + startTime;
        final String END_ROW_KEY = userID + endTime;

        // 获取数据，范围查询，依据为RowKey
        List<Map<String, String>> res = hbaseDAO.getData(
                TABLE_NAME,
                START_ROW_KEY,
                END_ROW_KEY
        );

        List<String> newsIDs = new ArrayList<>();

        for (Map<String, String> row : res) {
            String newsID = row.get(CF_NAME + ":" + COL_NAME_NEWS_ID);
            newsIDs.add(newsID);
        }

        return newsIDs;
    }

    @Override
    public List<String> getClickedNewsIDsByCategory(String category,
                                                    String startTime,
                                                    String endTime) throws Exception {
        final String TABLE_NAME = "category_clicks";
        final String CF_NAME = "info";
        final String COL_NAME_CATEGORY = "category";
        final String COL_NAME_NEWS_ID = "news_id";
        final String COL_NAME_EXPOSURETIME = "exposure_time";

        final String START_ROW_KEY = category + startTime;
        final String END_ROW_KEY = category + endTime;

        List<Map<String, String>> res = hbaseDAO.getData(
                TABLE_NAME,
                START_ROW_KEY,
                END_ROW_KEY
        );

        List<String> newsIDs = new ArrayList<>();
        for (Map<String, String> row : res) {
            String newsID = row.get(CF_NAME + ":" + COL_NAME_NEWS_ID);
            newsIDs.add(newsID);
        }

        return newsIDs;
    }


    /**
     * 轻量级的获取新闻种类
     * 根据新闻ID查询新闻种类
     * 似乎有些浪费
     * 弃用
     *
     * @param newsID
     * @return String
     * @throws Exception
     */
    @Override
    @Deprecated
    public String getNewsCategory(String newsID) throws Exception {

        if (newsID == null)
            return null;

        final String TABLE_NAME = "news_info";
        final String CF_NAME = "info";
        final String COL_NAME_NEWS_ID = "news_id";
        final String COL_NAME_CATEGORY = "category";

        final String redisKey = newsID + ":" + COL_NAME_CATEGORY;
        if (redisDAO.exists(redisKey)) {
            return redisDAO.get(redisKey, String.class);
        }

        // 获取数据
        Map<String, String> res = hbaseDAO.getData(TABLE_NAME, newsID);
        String category = res.get(CF_NAME + ":" + COL_NAME_CATEGORY);
        redisDAO.set(redisKey, category);

        return category;
    }

    @Override
    public List<Clicks> getCategoryTrend(String category,
                                         String startTime,
                                         String endTime) {
        // 加入空值处理
        /**
         *
         *
         *
         *
         *
         *
         */

        final String TABLE_NAME = "category_clicks";
        final String CF_NAME = "info";
        final String COL_NAME_CATEGORY = "category";
        final String COL_NAME_EXPOSURETIME = "exposure_time";
        final String COL_NAME_NEWS_ID = "news_id";

        final String START_ROW_KEY = category + startTime;
        final String END_ROW_KEY = category + endTime;

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
                .append(CF_NAME).append(":").append(COL_NAME_CATEGORY).append(" = '").append(category).
        append(" AND START_ROW_KEY= ").append(START_ROW_KEY).append(" AND END_ROW_KEY= ")
                .append(END_ROW_KEY).append("';\n");

        // 将查询内容记录到查询信息中
        queryInfo.append("Query details:\n");
        queryInfo.append("  - Table: ").append(TABLE_NAME).append("\n");
        queryInfo.append("  - Column Family: ").append(CF_NAME).append("\n");
        queryInfo.append("  - Column Names: ").
                append(COL_NAME_EXPOSURETIME).append(",").
                append(COL_NAME_NEWS_ID).append("\n");

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

        long t = System.currentTimeMillis();
        // 获取数据，范围查询，依据为RowKey
        List<Map<String, String>> res = hbaseDAO.getData(
                TABLE_NAME,
                START_ROW_KEY,
                END_ROW_KEY
        );
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();


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
                    break;
                }
            }
        }

        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();


        // 存储到对象当中
        for (Instant instant : instants) {
            clicks.add(
                    new Clicks(instant, clickCounts.get(instant))
            );
        }


        // 结束查询，记录时间
        logger.stop();

        // 查询记录日志
        logger.writeToMySQL(mysqlDAO);


        /* Redis TBD

        redisDAO.set(redisKey, newsHistory);
         */
        return clicks;
    }
}
