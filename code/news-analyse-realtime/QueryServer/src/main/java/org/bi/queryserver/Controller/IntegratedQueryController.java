package org.bi.queryserver.Controller;

import org.bi.queryserver.Domain.Clicks;
import org.bi.queryserver.Domain.ReceiveDS;
import org.bi.queryserver.Service.impl.IntegratedQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/intgr_query")
public class IntegratedQueryController {

    @Autowired
    private IntegratedQueryService integratedQueryService;

    @PostMapping("/test")
    public List<Clicks> test(@RequestBody ReceiveDS receiveDS) throws Exception {
        System.out.println("receiveDS.getEndTime()");

        String[] userIDs = {"U335175"};
        String[] categories = {};
        int titleMinLen = 0;
        int titleMaxLen = Integer.MAX_VALUE;
        int bodyMinLen = 0;
        int bodyMaxLen = Integer.MAX_VALUE;
        String startTime = "2019-06-13 00:00:00";
        String endTime = "2019-07-13 23:59:59";
        return integratedQueryService.integratedQuery(
                userIDs,
                categories,
                startTime,
                endTime,
                titleMinLen,
                titleMaxLen,
                bodyMinLen,
                bodyMaxLen
        );

    }
}
