package im.prize.api.application;

import im.prize.api.interfaces.response.GuavaBuildingDetailResponse;
import im.prize.api.interfaces.response.GuavaSummaryResponse;

import java.util.List;

public interface GuavaSummaryService {

    List<GuavaSummaryResponse> getSummary(Integer level,
                                          Double northEastLng,
                                          Double northEastLat,
                                          Double southWestLng,
                                          Double southWestLat,
                                          Integer startArea,
                                          Integer endArea);

//    GuavaSearchResponse getBuilding(String buildingId);

    GuavaBuildingDetailResponse getBuildingDetail(String buildingId);

//    GuavaSearchResponse getRegion(String regionId);

//    List<GuavaTradeResponse> getRegionTrade(String regionId, Integer page);

//    List<GuavaTradeResponse> getRegionTrade(String regionId, Integer page, Integer startArea, Integer endArea, String date);

//    GuavaSearchResponse getRegion(Double lat, Double lng);

//    List<GuavaSearchResponse> getRegions(String query);
//
//    List<GuavaSearchResponse> getBuildings(String query);

//    List<GuavaChartResponse> getChartList(String buildingId, String areaId, Long beforeMonth);

//    List<GuavaTradeResponse> getBuildingTradeList(String buildingId, Integer page, String areaId, String date);

//    List<GuavaTradeResponse> getBuildingMarketList(String buildingId, Integer page, String areaId);

//    List<GuavaChartResponse> getRegionChartList(String regionId, Integer startArea, Integer endArea, Long beforeMonth);

//    List<GuavaTradeResponse> getRegionMarketList(String regionId, Integer page, Integer startArea, Integer endArea);

//    List<GuavaSearchResponse> getRegionChilds(String regionCode, RegionType regionType);

//    List<GuavaSearchResponse> getPlaceBuildings(String regionId);
}
