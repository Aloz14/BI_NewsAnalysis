package org.bi.queryserver.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.bi.queryserver.DAO.Response;
import org.bi.queryserver.Domain.Clicks;
import org.bi.queryserver.Domain.Data;
import org.bi.queryserver.Domain.Group;
import org.bi.queryserver.Domain.EnhancedData;
import org.bi.queryserver.Service.impl.IntegratedQueryService;
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

    @PostMapping("/intgr_query")
    public ResponseEntity<Response<List<Clicks>>> test(@RequestBody EnhancedData data, HttpServletRequest request) throws Exception {
        Group group=data.getData().getGroup().get(0);
        List<String> userIDs=new ArrayList<>();
        for (String userid : group.getUser_id()) {
            userIDs.add(userid);
        }
        String[] categories = {};
        int titleMinLen = 0;
        int titleMaxLen = Integer.MAX_VALUE;
        int bodyMinLen = 0;
        int bodyMaxLen = Integer.MAX_VALUE;
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
        return new ResponseEntity<>(Response.success(clicks,"The data returned"),HttpStatus.OK);
    }
}
