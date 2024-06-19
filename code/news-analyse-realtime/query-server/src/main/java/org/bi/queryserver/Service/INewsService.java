package org.bi.queryserver.Service;

import org.bi.queryserver.Domain.Clicks;
import org.bi.queryserver.Domain.NewsInfo;

import java.util.List;

public interface INewsService {
    List<Clicks> getClicksHistory(String newsID,
                                  String startTime,
                                  String endTime) throws Exception;

    List<NewsInfo> getNewsInfo(String[] newsIDs) throws Exception;
    List<NewsInfo> getNewsInfo(List<String> newsIDs) throws Exception;
    NewsInfo getNewsInfo(String newsID) throws Exception;

    List<String> getClickedNewsIDsByUserID(String userID,
                                           String startTime,
                                           String endTime) throws Exception;

    List<String> getClickedNewsIDsByCategory(String category,
                                             String startTime,
                                             String endTime) throws Exception;

    List<Clicks> getCategoryTrend(String category,
                                  String startTime,
                                  String endTime);

    @Deprecated
    String getNewsCategory(String newsID) throws Exception;
}
