package net.moboo.batch.application.service;

import net.moboo.batch.hgnn.repository.ApartmentMatchTable;

import java.util.List;

public interface TradeMatchService {
    List<ApartmentMatchTable> read();

    ApartmentMatchTable process(ApartmentMatchTable apartmentMatchTable);

    List<ApartmentMatchTable> write(List<ApartmentMatchTable> apartmentMatchTableList);
}
