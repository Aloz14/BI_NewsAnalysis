package org.bi.queryserver.Domain;

import java.util.List;

/**
 * 冗余的数据结构
 * 可能弃用
 */
@Deprecated
public class UserHistory {
    private String userID;
    private List<NewsHistory> newsHistory;

    public UserHistory(String userID) {
        this.userID = userID;
    }

    public UserHistory(String userID, List<NewsHistory> newsHistory) {
        this.userID = userID;
        this.newsHistory = newsHistory;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<NewsHistory> getNewsHistory() {
        return newsHistory;
    }

    public void setNewsHistory(List<NewsHistory> newsHistory) {
        this.newsHistory = newsHistory;
    }

    @Override
    public String toString() {
        return "UserHistory{" +
                "userID='" + userID + '\'' +
                ", newsHistory=" + newsHistory +
                '}';
    }
}
