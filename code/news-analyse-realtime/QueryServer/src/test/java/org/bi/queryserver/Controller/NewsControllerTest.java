package org.bi.queryserver.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class NewsControllerTest {
    @Autowired
    NewsController newsController;

    @Test
    public void testGetNewsHistoty() throws Exception {
        newsController.getNewsHistory("N100155");
    }

    @Test
    public void testGetNewsInfo() throws Exception {
        newsController.getNewsInfo("N10789");
    }
    @Test
    public void testGetCategory(){
        newsController.Trend("news","2019-06-01 00:00:00","2019-07-01 00:00:00");
    }


}
