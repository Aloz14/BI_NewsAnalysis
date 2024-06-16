package org.bi.queryserver.Domain;

import java.util.List;

public class Group {
    //List<String> newsTheme;
    //List<String> title_length;
    //List<String> news_length;
    List<String> user_id;
    //List<String> news_id;

//    @Override
//    public String toString() {
//        return "DataGroup{" +
//                "newsTheme=" + newsTheme +
//                ", title_length=" + title_length +
//                ", news_length=" + news_length +
//                ", user_id=" + user_id +
//                ", news_id=" + news_id +
//                '}';
//    }

//    public List<String> getNewsTheme() {
//        return newsTheme;
//    }
//
//    public void setNewsTheme(List<String> newsTheme) {
//        this.newsTheme = newsTheme;
//    }
//
//    public List<String> getTitle_length() {
//        return title_length;
//    }
//
//    public void setTitle_length(List<String> title_length) {
//        this.title_length = title_length;
//    }
//
//    public List<String> getNews_length() {
//        return news_length;
//    }
//
//    public void setNews_length(List<String> news_length) {
//        this.news_length = news_length;
//    }

    public Group() {
    }

    public Group(List<String> user_id) {
        this.user_id = user_id;
    }

    public List<String> getUser_id() {
        return user_id;
    }

    public void setUser_id(List<String> user_id) {
        this.user_id = user_id;
    }


//    public List<String> getNews_id() {
//        return news_id;
//    }
//
//    public void setNews_id(List<String> news_id) {
//        this.news_id = news_id;
//    }
}
