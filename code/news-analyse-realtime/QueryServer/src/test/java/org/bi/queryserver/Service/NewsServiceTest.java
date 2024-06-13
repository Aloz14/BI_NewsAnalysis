package org.bi.queryserver.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NewsServiceTest {
    @Autowired
    private NewsService newsService;

    @Test
    public void testGetNewsCategory() throws Exception {
        String newsID = "N10000";
        System.out.println(newsService.getNewsCategory(newsID)); ;
    }
}