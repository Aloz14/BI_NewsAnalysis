package org.bi.queryserver.Controller;

import org.bi.queryserver.Domain.NewsHistory;
import org.bi.queryserver.Service.NewsHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
@CrossOrigin
public class NewsHistoryController {

    @Autowired
    private NewsHistoryService newsHistoryService;


    @GetMapping("/history")
    public NewsHistory getNewsHistory(){
        NewsHistory history = newsHistoryService.getNewsHistory("N99806");
        return history;
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
