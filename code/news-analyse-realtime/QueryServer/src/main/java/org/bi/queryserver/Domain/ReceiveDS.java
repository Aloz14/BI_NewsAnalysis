package org.bi.queryserver.Domain;

public class ReceiveDS {
    String startTime;
    String endTime;

    public ReceiveDS(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
