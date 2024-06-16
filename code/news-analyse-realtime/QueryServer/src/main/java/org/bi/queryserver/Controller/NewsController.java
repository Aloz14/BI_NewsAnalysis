package org.bi.queryserver.Controller;

import org.bi.queryserver.Domain.Clicks;
import org.bi.queryserver.Domain.NewsInfo;
import org.bi.queryserver.Service.impl.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
@CrossOrigin
public class NewsController {

    @Autowired
    private NewsService newsService;


    @GetMapping("/history/{newsID}")
    public List<Clicks> getClicksHistory(@PathVariable String newsID) throws Exception {
        String startTime = "2019-06-13 00:00:00";
        String endTime = "2019-07-13 23:59:59";
        return newsService.getClicksHistory(newsID, startTime, endTime);
    }

    @GetMapping("/info/{newsID}")
    public NewsInfo getNewsInfo(@PathVariable String newsID) throws Exception {

        return newsService.getNewsInfo(newsID);
    }

    @GetMapping("/test")
    public String test() {
        newsService.testSpark();
        return "test";
    }

    @GetMapping("/trend/{category}")
    public List<Clicks> getCategoryTrend(@PathVariable String category){
        String startTime = "2019-06-13 00:00:00";
        String endTime = "2019-07-13 23:59:59";
        return newsService.getCategoryTrend(category,startTime,endTime);
    }


}
