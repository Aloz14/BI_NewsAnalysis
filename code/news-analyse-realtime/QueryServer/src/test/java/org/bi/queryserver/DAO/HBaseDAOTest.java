package org.bi.queryserver.DAO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HBaseDAOTest {

    @Autowired
    private HBaseDAO hBaseDAO;

    @Test
    public void testGetData() {
        String tableName = "news_clicks";
        String newsID = "N100155";
        String startTime = "2019-06-13 00:00:00";
        String endTime = "2019-07-13 23:59:59";
        String startRowKey = newsID + startTime;
        String endRowKey = newsID + endTime;

        System.out.println(hBaseDAO.getData(tableName, startRowKey, endRowKey));
    }
}
