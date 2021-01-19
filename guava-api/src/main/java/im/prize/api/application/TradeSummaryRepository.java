package im.prize.api.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeSummaryRepository extends JpaRepository<TradeSummary, Long>, JpaSpecificationExecutor<TradeSummary> {
    List<TradeSummary> findByBuildingCode(String buildingCode);

    Page<TradeSummary> findByBuildingCode(String buildingCode, Pageable pageable);

    Page<TradeSummary> findByBuildingCodeAndAreaCode(String buildingCode, String areaCode, Pageable pageable);

    Optional<TradeSummary> findTop1ByBuildingCodeAndAreaTypeOrderByPriceDesc(String buildingCode, String areaType);

    Optional<TradeSummary> findTop1ByBuildingCodeOrderByDateDesc(String buildingCode);

    Optional<TradeSummary> findTop1ByBuildingCodeAndPrivateAreaGreaterThanEqualAndPrivateAreaLessThanEqualOrderByDateDesc(String buildingCode,
                                                                                                                          Double startArea,
                                                                                                                          Double endArea);

    List<TradeSummary> findTop100ByRegionCodeLikeAndPrivateAreaIsNotNullOrderByDateDesc(String regionCode);

    List<TradeSummary> findTop100ByRegionCodeLikeAndPrivateAreaGreaterThanEqualAndPrivateAreaLessThanEqualOrderByDateDesc(String regionCode,
                                                                                                                          Double startArea,
                                                                                                                          Double endArea);
}
