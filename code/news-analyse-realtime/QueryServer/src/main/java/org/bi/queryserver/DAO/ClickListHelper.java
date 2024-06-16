package org.bi.queryserver.DAO;

import org.bi.queryserver.Domain.Favor;

import java.util.List;

public class ClickListHelper {
    public void setClickList(List<Favor> clickList) {
        ClickList = clickList;
    }

    public ClickListHelper() {
    }

    public ClickListHelper(List<Favor> clickList) {
        ClickList = clickList;
    }

    public List<Favor> getClickList() {
        return ClickList;
    }

    List<Favor> ClickList;
}
