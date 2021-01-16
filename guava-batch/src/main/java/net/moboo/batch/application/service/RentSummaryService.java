package net.moboo.batch.application.service;

import net.moboo.batch.wooa.repository.BuildingMapping;
import net.moboo.batch.wooa.repository.RentSummary;
import net.moboo.batch.wooa.repository.TradeSummary;

import java.util.List;

public interface RentSummaryService {
    List<RentSummary> read();

    List<RentSummary> write(List<List<RentSummary>> tradeSummaries);

    List<RentSummary> process(BuildingMapping buildingMapping);
}
