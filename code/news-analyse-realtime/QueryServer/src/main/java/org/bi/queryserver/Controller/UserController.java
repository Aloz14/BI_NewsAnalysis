package org.bi.queryserver.Controller;

import org.bi.queryserver.Service.UserService;
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
    public void getUserHistory(@PathVariable("userID") String userID) throws Exception {
        String startTime = "2019-06-13 00:00:00";
        String endTime = "2019-07-13 23:59:59";
        userService.getUserHistory(userID,startTime,endTime);
    }



}
