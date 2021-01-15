package net.moboo.batch.application.service;

import net.moboo.batch.wooa.repository.TradeSummary;
import net.moboo.batch.wooa.repository.BuildingMapping;

import java.util.List;

public interface TradeSummaryService {
    List<TradeSummary> read();

    List<TradeSummary> write(List<List<TradeSummary>> tradeSummaries);

    List<TradeSummary> process(BuildingMapping buildingMapping);
}
