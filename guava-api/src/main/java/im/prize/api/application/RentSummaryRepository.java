package im.prize.api.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentSummaryRepository extends JpaRepository<RentSummary, Long>, JpaSpecificationExecutor<RentSummary> {
    List<RentSummary> findByBuildingCode(String buildingCode);
}
