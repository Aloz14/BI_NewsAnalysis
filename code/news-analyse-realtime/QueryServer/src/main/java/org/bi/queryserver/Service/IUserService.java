package org.bi.queryserver.Service;

import org.bi.queryserver.Domain.Favor;

import java.util.List;

public interface IUserService {
    List<Favor> getUserHistory(String userID,
                               String startTime,
                               String endTime) throws Exception;
}
