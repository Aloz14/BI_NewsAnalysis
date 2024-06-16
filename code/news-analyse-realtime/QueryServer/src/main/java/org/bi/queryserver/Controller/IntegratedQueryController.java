package org.bi.queryserver.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.bi.queryserver.DAO.GroupHelper;
import org.bi.queryserver.DAO.Response;
import org.bi.queryserver.DAO.DataHelper;
import org.bi.queryserver.Domain.Clicks;
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
    public ResponseEntity<Response<GroupHelper>> test(@RequestBody EnhancedData data, HttpServletRequest request) throws Exception {
        List<Group> groups=data.getData().getGroup();
        List<DataHelper> helpers=new ArrayList<>();
        for (Group group : groups) {
            List<String> userIDs=new ArrayList<>();
            if(group.getUser_id()!=null) {
                for (String userid : group.getUser_id()) {
                    userIDs.add(userid);
                }
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
            DataHelper dataHelper = new DataHelper(clicks);
            helpers.add(dataHelper);
        }
        GroupHelper groupHelper = new GroupHelper(helpers);
        return new ResponseEntity<>(Response.success(groupHelper,"The data returned"),HttpStatus.OK);
    }
}
