package org.bi.queryserver.Domain;

import java.time.Instant;

/**
 * 一个类似Map的数据结构
 * 实现从TimeStamp到ClicksCnt的读取
 */
public class Clicks {
    private Instant timestamp;
    private int clicks;

    public Clicks(Instant timestamp, int clicks) {
        this.clicks = clicks;
        this.timestamp = timestamp;
    }
    public int getClicks() {
        return clicks;
    }
    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
