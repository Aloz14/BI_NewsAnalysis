package org.bi.queryserver.Domain;

import java.util.List;

public class reList {
    List<NewsInfo> recommendList;

    public List<NewsInfo> getRecommendList() {
        return recommendList;
    }

    public void setRecommendList(List<NewsInfo> recommendList) {
        this.recommendList = recommendList;
    }

    public reList() {
    }

    public reList(List<NewsInfo> recommendList) {
        this.recommendList = recommendList;
    }
}
