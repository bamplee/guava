package net.moboo.batch.infrastructure.jpa;

import net.moboo.batch.hgnn.repository.GuavaRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GuavaRegionStatsRepository extends JpaRepository<GuavaRegionStats, Long>, JpaSpecificationExecutor<GuavaRegion> {
    Optional<GuavaRegionStats> findByRegionCode(String regionCode);
}
