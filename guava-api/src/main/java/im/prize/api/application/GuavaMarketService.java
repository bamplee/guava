package im.prize.api.application;

import im.prize.api.interfaces.response.GuavaTradeResponse;

import java.util.List;

public interface GuavaMarketService {
    List<GuavaTradeResponse> getRegionMarketList(String tradeType, String regionId, Integer page, Integer size, Integer startArea, Integer endArea);

    List<GuavaTradeResponse> getBuildingMarketList(String tradeType, String buildingId, Integer page, Integer size, String areaId);
}
