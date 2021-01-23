package im.prize.api.application;

import im.prize.api.interfaces.response.GuavaChartResponse;

import java.util.List;

public interface GuavaTradeChartService {
    List<GuavaChartResponse> getRegionChartList(String tradeType,
                                                String regionId,
                                                Integer startArea,
                                                Integer endArea,
                                                String startDate,
                                                String endDate);

    List<GuavaChartResponse> getBuildingChartList(String tradeType,
                                                  String buildingId,
                                                  Integer startArea,
                                                  Integer endArea,
                                                  String startDate,
                                                  String endDate);

    List<GuavaChartResponse> getChartList(String tradeType, String buildingId, String areaId, String startDate, String endDate);
}
