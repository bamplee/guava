package net.moboo.batch.application.service;

import net.moboo.batch.domain.RegionType;

import java.time.YearMonth;

public interface TicketSyncService {
    void syncOpenApiList(YearMonth yearMonth);

    void syncDataList(YearMonth yearMonth);

    void syncTradeStatsList(YearMonth yearMonth, RegionType regionType);

    void setMaxPrice(YearMonth yearMonth);
}
