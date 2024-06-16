package org.bi.queryserver.DAO;

import java.util.List;

public class GroupHelper {
    List<DataHelper> group;

    public GroupHelper(List<DataHelper> group) {
        this.group = group;
    }

    public GroupHelper() {
    }

    public List<DataHelper> getGroup() {
        return group;
    }

    public void setGroup(List<DataHelper> group) {
        this.group = group;
    }
}
