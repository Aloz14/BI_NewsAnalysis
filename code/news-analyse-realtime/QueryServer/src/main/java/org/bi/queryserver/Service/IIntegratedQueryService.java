package org.bi.queryserver.Service;

import org.bi.queryserver.Domain.Clicks;

import java.util.List;

public interface IIntegratedQueryService {
    public List<Clicks> integratedQuery(String[] userIDs,
                                       String[] newsCategories,
                                       String startTime,
                                       String endTime,
                                       int titleMinLen,
                                       int titleMaxLen,
                                       int bodyMinLen,
                                       int bodyMaxLen);
}
