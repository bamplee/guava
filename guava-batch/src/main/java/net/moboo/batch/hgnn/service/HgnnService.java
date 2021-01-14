//package net.moboo.batch.hgnn.service;
//
//import net.moboo.batch.hgnn.repository.ApartmentMatchTable;
//import net.moboo.batch.hgnn.repository.AptTemp;
//import net.moboo.batch.hgnn.repository.GuavaBuilding;
//import net.moboo.batch.hgnn.repository.GuavaRegion;
//import net.moboo.batch.infrastructure.jpa.PbRegionCode;
//
//import java.util.List;
//
//public interface HgnnService {
//
//    List<ApartmentMatchTable> getAllApartmentMatchTableList();
//
//    ApartmentMatchTable transform(ApartmentMatchTable apartmentMatchTable);
//
//    List<ApartmentMatchTable> setApartmentMatchTables(List<ApartmentMatchTable> apartmentMatchTables);
//
//    void test();
//
//    void fetchHgnnRegion();
//
//    List<AptTemp> read();
//    List<GuavaRegion> readBuilding();
//
//    GuavaRegion process(PbRegionCode pbRegionCode);
//    GuavaBuilding process(GuavaRegion guavaHgnnBuilding);
//
//    List<GuavaRegion> write(List<GuavaRegion> guavaRegions);
//    List<GuavaBuilding> writeBuilding(List<GuavaBuilding> guavaRegions);
//    void sync();
//}
