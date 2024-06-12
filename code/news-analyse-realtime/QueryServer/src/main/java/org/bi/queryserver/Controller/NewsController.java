package org.bi.queryserver.Controller;

import org.bi.queryserver.Domain.NewsHistory;
import org.bi.queryserver.Domain.NewsInfo;
import org.bi.queryserver.Service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/news")
@CrossOrigin
public class NewsController {

    @Autowired
    private NewsService newsService;


    @GetMapping("/history/{newsID}")
    public NewsHistory getNewsHistory(@PathVariable String newsID) throws Exception {
        return newsService.getNewsHistory(newsID);
    }

    @GetMapping("/info/{newsID}")
    public NewsInfo getNewsInfo(@PathVariable String newsID) throws Exception {
        return newsService.getNewsInfo(newsID);
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
