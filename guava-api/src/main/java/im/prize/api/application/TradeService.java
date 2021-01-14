package im.prize.api.application;

import im.prize.api.application.dto.TradeDto;

import java.util.List;

public interface TradeService {
    List<TradeDto> getTrade(String aptId, Integer page, String area);
    List<TradeDto> getNaverTrade(String aptId, Integer page);

    List<TradeDto> getChartData(String placeId, Integer beforeMonths);
}
