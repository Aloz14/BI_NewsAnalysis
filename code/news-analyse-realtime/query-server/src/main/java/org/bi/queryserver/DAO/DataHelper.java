package org.bi.queryserver.DAO;

import org.bi.queryserver.Domain.Clicks;

import java.util.List;

public class DataHelper {
    List<Clicks> data;

    public DataHelper(List<Clicks> data) {
        this.data = data;
    }

    public DataHelper() {
    }

    public List<Clicks> getData() {
        return data;
    }

    public void setData(List<Clicks> data) {
        this.data = data;
    }
}
