package org.bi.queryserver.Domain;

public class NewsInfo {
    private String newsID;
    private String category;
    private String topic;
    private String headline;
    private String newsBody;

    public NewsInfo(String newsID, String category, String topic, String headline, String newsBody) {
        this.newsID = newsID;
        this.category = category;
        this.topic = topic;
        this.headline = headline;
        this.newsBody = newsBody;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getNewsBody() {
        return newsBody;
    }

    public void setNewsBody(String newsBody) {
        this.newsBody = newsBody;
    }

    @Override
    public String toString() {
        return "NewsInfo{" +
                "newsID='" + newsID + '\'' +
                ", category='" + category + '\'' +
                ", topic='" + topic + '\'' +
                ", headline='" + headline + '\'' +
                ", newsBody='" + newsBody + '\'' +
                '}';
    }
}
