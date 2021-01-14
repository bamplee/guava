package net.moboo.batch.application.service;

import net.moboo.batch.domain.OpenApiRentInfo;
import net.moboo.batch.domain.RegionType;
import net.moboo.batch.infrastructure.jpa.GuavaRegionStats;

import java.time.YearMonth;
import java.util.List;

public interface RentSyncService {
//    void syncOpenApiList(YearMonth yearMonth);
//
//    void syncDataList(YearMonth yearMonth);
//
//    void syncTradeStatsList(YearMonth yearMonth, RegionType regionType);

    List<OpenApiRentInfo> getOpenApiRentInfo(YearMonth yearMonth);

    List<OpenApiRentInfo> setOpenApiRentInfo(List<OpenApiRentInfo> openApiRentInfos);

    OpenApiRentInfo process(OpenApiRentInfo openApiRentInfo);

}
