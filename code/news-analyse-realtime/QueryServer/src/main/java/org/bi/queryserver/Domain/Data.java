package org.bi.queryserver.Domain;

import java.util.List;

public class Data {
    public String start_time;
    public String end_time;
    public String getStart_time() {
        return start_time;
    }

    public Data() {
    }

    public Data(String start_time, String end_time, List<Group> group) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.group = group;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }


    public List<Group> group;

    public List<Group> getGroup() {
        return group;
    }

    public void setGroup(List<Group> group) {
        this.group = group;
    }
}
