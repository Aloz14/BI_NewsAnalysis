package org.bi.queryserver.Controller;

import org.bi.queryserver.Domain.Favor;
import org.bi.queryserver.Domain.NewsInfo;
import org.bi.queryserver.Service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/history/{userID}")
    public List<Favor> getUserFavors(@PathVariable("userID") String userID) throws Exception {
        String startTime = "2019-06-13 00:00:00";
        String endTime = "2019-06-22 20:10:59";
        return userService.getUserFavors(userID, startTime, endTime);
    }

    @GetMapping("/recommend/{userID}")
    public List<NewsInfo> getRecommendations(@PathVariable("userID") String userID) throws Exception {
        return userService.getRecommendations(userID);

    }


}
