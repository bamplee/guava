package im.prize.api.application;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentSummaryRepository extends JpaRepository<RentSummary, Long>, JpaSpecificationExecutor<RentSummary> {
    List<RentSummary> findByBuildingCode(String buildingCode);

    Optional<RentSummary> findTop1ByBuildingCodeAndAreaTypeOrderByPriceDesc(String buildingCode, String areaType);
}
