package im.prize.api.application;

import im.prize.api.interfaces.response.GuavaTradeResponse;

import java.util.List;

public interface GuavaMarketService {
    List<GuavaTradeResponse> getRegionMarketList(String regionId, Integer page, Integer startArea, Integer endArea);

    List<GuavaTradeResponse> getBuildingMarketList(String buildingId, Integer page, String areaId);
}
