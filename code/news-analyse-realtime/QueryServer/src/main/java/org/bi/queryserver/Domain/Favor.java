package org.bi.queryserver.Domain;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Favor {
    private Instant timestamp;
    private String mostPopCategory;
    private int mostPopClicks;
    private Map<String, Integer> categoryCounts;

    public Favor(Instant timestamp) {
        this.timestamp = timestamp;
        categoryCounts = new HashMap<>();
        mostPopCategory = null;
        mostPopClicks = 0;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMostPopCategory() {
        if (mostPopCategory == null) {
            calMostPopCategory();
        }
        return mostPopCategory;
    }

    public void calMostPopCategory() {
        for (String category : categoryCounts.keySet()) {
            if (categoryCounts.get(category) > mostPopClicks) {
                mostPopCategory = category;
                mostPopClicks = categoryCounts.get(category);
            }
        }
    }

    public Map<String, Integer> getCategoryCounts() {
        return categoryCounts;
    }

    public int getMostPopClicks() {
        if (mostPopClicks == 0) {
            calMostPopCategory();
        }
        return mostPopClicks;
    }

    public void addCategoryCount(String category) {
        if (categoryCounts.containsKey(category)) {
            categoryCounts.put(category, categoryCounts.get(category) + 1);
        } else {
            categoryCounts.put(category, 1);
        }
    }


}
