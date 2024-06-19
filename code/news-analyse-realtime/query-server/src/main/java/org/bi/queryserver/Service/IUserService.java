package org.bi.queryserver.Service;

import org.bi.queryserver.Domain.Favor;
import org.bi.queryserver.Domain.NewsInfo;

import java.util.List;

public interface IUserService {
    List<Favor> getUserFavors(String userID,
                              String startTime,
                              String endTime) throws Exception;

    List<NewsInfo> getRecommendations(String userID) throws Exception;
}
