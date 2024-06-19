package org.bi.queryserver.Domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 冗余的数据结构
 * 可能弃用
 */
@Deprecated
public class NewsHistory {
    private String newsID;
    private List<Exposure> exposures;

    public NewsHistory(String newsID) {
        this.newsID = newsID;
        exposures = new ArrayList<>();
    }

    public NewsHistory(String newsID, List<Exposure> exposures) {
        this.newsID = newsID;
        this.exposures = exposures;
    }

    public String getNewsID() {
        return newsID;
    }

    public List<Exposure> getExposures() {
        return exposures;
    }

    public boolean addExposure(Instant exposureTime, Integer dwellTime) {
        return exposures.add(new Exposure(exposureTime, dwellTime));
    }

    public boolean addExposure(Exposure e) {
        return exposures.add(e);
    }

    @Override
    public String toString() {
        return "NewsHistory{" +
                "newsID='" + newsID + '\'' +
                ", exposures=" + exposures +
                '}';
    }
}
