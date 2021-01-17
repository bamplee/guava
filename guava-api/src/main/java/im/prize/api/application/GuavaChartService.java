package im.prize.api.application;

import im.prize.api.interfaces.response.GuavaChartResponse;

import java.util.List;

public interface GuavaChartService {
    List<GuavaChartResponse> getRegionChartList(String regionId, Integer startArea, Integer endArea, Long beforeMonth);

    List<GuavaChartResponse> getChartList(String buildingId, String areaId, String startDate, String endDate);
}
