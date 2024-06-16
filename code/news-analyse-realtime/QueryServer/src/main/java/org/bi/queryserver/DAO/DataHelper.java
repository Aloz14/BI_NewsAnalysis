package org.bi.queryserver.DAO;

import org.bi.queryserver.Domain.Clicks;

import java.util.List;

public class DataHelper {
    List<Clicks> data;

    public DataHelper(List<Clicks> data, String newsTheme, String titleLength, String newsLength, String userID) {
        this.data = data;
        this.newsTheme = newsTheme;
        this.titleLength = titleLength;
        this.newsLength = newsLength;
        this.userID = userID;
    }

    public String getNewsTheme() {
        return newsTheme;
    }

    public void setNewsTheme(String newsTheme) {
        this.newsTheme = newsTheme;
    }

    public String getTitleLength() {
        return titleLength;
    }

    public void setTitleLength(String titleLength) {
        this.titleLength = titleLength;
    }

    public String getNewsLength() {
        return newsLength;
    }

    public void setNewsLength(String newsLength) {
        this.newsLength = newsLength;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    String newsTheme;
    String titleLength;
    String newsLength;
    String userID;
    public DataHelper(List<Clicks> data) {
        this.data = data;
    }

    public DataHelper() {
    }

    public List<Clicks> getData() {
        return data;
    }

    public void setData(List<Clicks> data) {
        this.data = data;
    }
}
