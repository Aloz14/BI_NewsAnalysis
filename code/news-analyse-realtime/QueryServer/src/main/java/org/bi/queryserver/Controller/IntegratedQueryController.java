package org.bi.queryserver.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.bi.queryserver.DAO.GroupHelper;
import org.bi.queryserver.DAO.Response;
import org.bi.queryserver.DAO.DataHelper;
import org.bi.queryserver.Domain.Clicks;
import org.bi.queryserver.Domain.Group;
import org.bi.queryserver.Domain.EnhancedData;
import org.bi.queryserver.Service.impl.IntegratedQueryService;
import org.bi.queryserver.Service.impl.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class IntegratedQueryController {

    @Autowired
    private IntegratedQueryService integratedQueryService;
    @Autowired
    private NewsService newsService;
    @PostMapping("/intgr_query")
    public ResponseEntity<Response<GroupHelper>> test(@RequestBody EnhancedData data, HttpServletRequest request) throws Exception {
        List<Group> groups=data.getData().getGroup();
        List<DataHelper> helpers=new ArrayList<>();
        GroupHelper groupHelper;
        List<String> categories = new ArrayList<>();
        int titleMinLen = 0;
        int titleMaxLen = Integer.MAX_VALUE;
        int bodyMinLen = 0;
        int bodyMaxLen = Integer.MAX_VALUE;
        for (Group group : groups) {
            List<String> userIDs=new ArrayList<>();
            List<String> newsIDs=new ArrayList<>();

            if(group.getNews_length()!=null){
                for (String len : group.getNews_length()) {
                    bodyMaxLen=Math.min(Integer.valueOf(len),Integer.valueOf(bodyMaxLen));
                }
            }
            if(group.getTitle_length()!=null){
                for (String len : group.getTitle_length()) {
                    titleMaxLen=Math.min(Integer.valueOf(len),Integer.valueOf(titleMaxLen));
                }
            }
            if(group.getNews_id()==null&&group.getTitle_length()==null&&
            group.getUser_id()==null&&group.getNews_length()==null) {
                if (group.getNews_theme() != null) {
                    for (String category : group.getNews_theme()) {
                        List<Clicks> clicks = newsService.getCategoryTrend(category, data.getData().getStart_time(), data.getData().getEnd_time());
                        DataHelper dataHelper = new DataHelper(clicks);
                        helpers.add(dataHelper);
                    }
                    break;
                }
            }else{
                if (group.getNews_theme() != null) {
                    for (String category : group.getNews_theme()) {
                        categories.add(category);
                    }
                }
            }
            if(group.getNews_id()!=null){
                for (String newsID : group.getNews_id()) {
                    List<Clicks> clicks = newsService.getClicksHistory(newsID, data.getData().getStart_time(), data.getData().getEnd_time());
                    DataHelper dataHelper = new DataHelper(clicks);
                    helpers.add(dataHelper);
                }
                break;
            }

            if(group.getUser_id()!=null) {
                for (String userid : group.getUser_id()) {
                    userIDs.add(userid);
                }
            }


            List<Clicks> clicks = integratedQueryService.integratedQuery(
                    userIDs,
                    categories,
                    data.getData().getStart_time(),
                    data.getData().getEnd_time(),
                    titleMinLen,
                    titleMaxLen,
                    bodyMinLen,
                    bodyMaxLen
            );
            DataHelper dataHelper = new DataHelper(clicks);
            helpers.add(dataHelper);
        }
        groupHelper = new GroupHelper(helpers);
        return new ResponseEntity<>(Response.success(groupHelper,"The data returned"),HttpStatus.OK);
    }
}
