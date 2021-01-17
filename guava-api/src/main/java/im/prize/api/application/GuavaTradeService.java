package im.prize.api.application;

import im.prize.api.interfaces.response.GuavaTradeResponse;

import java.util.List;

public interface GuavaTradeService {
    List<GuavaTradeResponse> getRegionTrade(String tradeType, String regionId, Integer page, Integer startArea, Integer endArea, String date);

    List<GuavaTradeResponse> getBuildingTradeList(String tradeType, String buildingId, Integer page, String areaId, String date);

//    List<GuavaTradeResponse> getBuildingTradeList(Integer page, GuavaTradeSearch guavaTradeRequest);
}
