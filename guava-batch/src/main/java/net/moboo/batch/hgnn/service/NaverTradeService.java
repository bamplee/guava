package net.moboo.batch.hgnn.service;

import net.moboo.batch.domain.TradeArticle;
import net.moboo.batch.domain.TradeItem;

import java.util.List;

public interface NaverTradeService {
    List<TradeArticle> read(String parentRegionCode);

    TradeArticle process(TradeArticle apartmentMatchTable);

    List<TradeArticle> write(List<TradeArticle> apartmentMatchTableList);
}
