package org.bi.queryserver.Service;


import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.bi.queryserver.DAO.HBaseDAO;
import org.bi.queryserver.Domain.NewsHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NewsHistoryService {
    final String TABLE_NAME = "news_clicks";
    final String CF_NAME = "info";
    final String COL_NAME_QUERY = "news_id";

    @Autowired
    HBaseDAO hbaseDAO;

    public NewsHistory getNewsHistory(String newsID){

        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(
                CF_NAME.getBytes(),      // 列族
                COL_NAME_QUERY.getBytes(),   // 列名
                CompareOperator.EQUAL,
                newsID.getBytes()
        );

        // 启用过滤
        singleColumnValueFilter.setFilterIfMissing(true);

        List<Map<String, String>> res = hbaseDAO.getData(TABLE_NAME,singleColumnValueFilter);
        System.out.println(res);



        return new NewsHistory(newsID);
    }

}
