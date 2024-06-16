package org.bi.queryserver.Service;

import org.bi.queryserver.Domain.Clicks;
import org.bi.queryserver.Domain.NewsInfo;

import java.util.List;

public interface INewsService {
    List<Clicks> getClicksHistory(String newsID,
                                  String startTime,
                                  String endTime) throws Exception;

    NewsInfo getNewsInfo(String newsID) throws Exception;

    List<String> getClickedNewsIDsByUserID(String userID,
                                           String startTime,
                                           String endTime) throws Exception;

    List<String> getClickedNewsIDsByCategory(String category,
                                             String startTime,
                                             String endTime) throws Exception;

    @Deprecated
    String getNewsCategory(String newsID) throws Exception;
}