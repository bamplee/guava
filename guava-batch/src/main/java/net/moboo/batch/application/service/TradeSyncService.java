package net.moboo.batch.application.service;

import net.moboo.batch.domain.OpenApiTradeInfo;
import net.moboo.batch.hgnn.repository.GuavaMappingInfo;
import net.moboo.batch.hgnn.repository.GuavaRegion;
import net.moboo.batch.infrastructure.jpa.GuavaRegionStats;

import java.time.YearMonth;
import java.util.List;

public interface TradeSyncService {
//    void syncOpenApiList(YearMonth yearMonth);
//
//    void syncDataList(YearMonth yearMonth);
//
//    void syncTradeStatsList(YearMonth yearMonth, RegionType regionType);
//
//    void setMaxPrice(YearMonth yearMonth);

    List<OpenApiTradeInfo> getOpenApiTradeInfo(YearMonth yearMonth);

    List<OpenApiTradeInfo> setOpenApiTradeInfo(List<OpenApiTradeInfo> openApiTradeInfos);

    OpenApiTradeInfo process(OpenApiTradeInfo openApiTradeInfo);

    List<GuavaRegionStats> tradeStatsRead();

//    GuavaRegionStats tradeStatsProcess(GuavaRegion guavaRegion);

    void tradeStatsWrite(List<GuavaRegionStats> tradeItems);
}
