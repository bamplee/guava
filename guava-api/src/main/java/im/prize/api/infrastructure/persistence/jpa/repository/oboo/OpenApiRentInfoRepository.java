package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.OpenApiRentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpenApiRentInfoRepository extends JpaRepository<OpenApiRentInfo, Long> {
    List<OpenApiRentInfo> findByYearAndMonth(String year, String month);

    List<OpenApiRentInfo> findByRegionCodeAndDongAndLotNumberAndAptName(String regionCode, String dong, String lotNumber, String aptName);
}
