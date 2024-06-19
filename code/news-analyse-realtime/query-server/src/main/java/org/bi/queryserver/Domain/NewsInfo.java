package org.bi.queryserver.Domain;

public class NewsInfo {
    private String newsID;
    private String category;
    private String topic;
    private int headlineLen;
    private int newsBodyLen;

    public NewsInfo(String newsID, String category, String topic, int headlineLen, int newsBodyLen){
        this.newsID = newsID;
        this.category = category;
        this.topic = topic;
        this.headlineLen = headlineLen;
        this.newsBodyLen = newsBodyLen;
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

    public int getHeadlineLen() {
        return headlineLen;
    }

    public void setHeadlineLen(int headlineLen) {
        this.headlineLen = headlineLen;
    }

    public int getNewsBodyLen() {
        return newsBodyLen;
    }

    public void setNewsBodyLen(int newsBodyLen) {
        this.newsBodyLen = newsBodyLen;
    }

    @Override
    public String toString() {
        return "NewsInfo{" +
                "newsID='" + newsID + '\'' +
                ", category='" + category + '\'' +
                ", topic='" + topic + '\'' +
                ", headlineLen=" + headlineLen +
                ", newsBodyLen=" + newsBodyLen +
                '}';
    }
}
