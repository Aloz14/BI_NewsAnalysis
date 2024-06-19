package org.bi.queryserver.Controller;

import org.bi.queryserver.DAO.ClickListHelper;
import org.bi.queryserver.DAO.Response;
import org.bi.queryserver.Domain.Favor;
import org.bi.queryserver.Domain.NewsInfo;
import org.bi.queryserver.Domain.reList;
import org.bi.queryserver.Service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/interestQuery")
    public Response<ClickListHelper> getUserFavors(@RequestParam("user_id") String user_id) throws Exception {
        String startTime = "2019-06-13 00:00:00";
        String endTime = "2019-06-22 20:10:59";
        List<Favor> userFavors = userService.getUserFavors(user_id, startTime, endTime);
        ClickListHelper clickListHelper = new ClickListHelper(userFavors);
        return Response.success(clickListHelper,"Here're your favorite news,Mr."+user_id);
    }

    @GetMapping("/recommendNews")
    public Response<reList> getRecommendations(@RequestParam("user_id") String userID) throws Exception {
        List<NewsInfo> recommendations = userService.getRecommendations(userID);
        reList reList = new reList(recommendations);
        return Response.success(reList,"Here're your recommended news,Mr. "+userID);

    }


}
