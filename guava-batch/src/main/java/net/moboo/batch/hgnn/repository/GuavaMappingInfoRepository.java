package net.moboo.batch.hgnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuavaMappingInfoRepository extends JpaRepository<GuavaMappingInfo, Long> {
    List<GuavaMappingInfo> findByAddress(String address);

    List<GuavaMappingInfo> findByRegionCodeStartsWithAndPortalIdIsNotNull(String regionCode);
    List<GuavaMappingInfo> findByRegionCodeStartsWithAndTypeAndPortalIdIsNotNull(String regionCode, Integer type);

    List<GuavaMappingInfo> findByRegionCode(String regionCode);

    List<GuavaMappingInfo> findByTypeAndRegionCodeAndLotNumberAndName(Integer type, String regionCode, String lotNumber, String name);

    List<GuavaMappingInfo> findByBuildingCodeIn(List<String> buildingCodeList);

    Optional<GuavaMappingInfo> findByRegionCodeAndBuildingCode(String buildingCode, String regionCode);
}
