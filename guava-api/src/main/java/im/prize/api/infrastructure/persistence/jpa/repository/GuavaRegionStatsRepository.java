package im.prize.api.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface GuavaRegionStatsRepository extends JpaRepository<GuavaRegionStats, Long>, JpaSpecificationExecutor<GuavaRegion> {
    Optional<GuavaRegionStats> findByRegionCode(String regionCode);
    List<GuavaRegionStats> findByRegionCodeLike(String regionCode);
}
