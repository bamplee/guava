package net.moboo.batch.infrastructure.jpa;

import net.moboo.batch.domain.OpenApiRentInfo;
import net.moboo.batch.domain.OpenApiTradeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpenApiRentInfoRepository extends JpaRepository<OpenApiRentInfo, Long> {
    List<OpenApiRentInfo> findByYearAndMonth(String year, String month);

    List<OpenApiRentInfo> findByRegionCodeAndLotNumberAndAptName(String regionCode,
                                                                 String lotNumber,
                                                                 String aptName);

    List<OpenApiRentInfo> findByRegionCodeAndDongAndLotNumber(String regionCode,
                                                              String dong,
                                                              String lotNumber);
}
