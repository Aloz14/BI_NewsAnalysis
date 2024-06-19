package org.bi.queryserver.Domain;

import java.time.Instant;

/**
 * 一个类似Map的数据结构
 * 实现从TimeStamp到ClicksCnt的读取
 */
public class Clicks {
    private Instant time;
    private int hit;

    public Clicks(Instant time, int hit) {
        this.hit = hit;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Clicks{" +
                "time=" + time +
                ", hit=" + hit +
                '}';
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
