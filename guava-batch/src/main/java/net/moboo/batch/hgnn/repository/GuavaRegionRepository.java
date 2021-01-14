package net.moboo.batch.hgnn.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuavaRegionRepository extends JpaRepository<GuavaRegion, Long> {
    @Cacheable("findByRegionCode")
    Optional<GuavaRegion> findByRegionCode(String regionCode);
    List<GuavaRegion> findByRegionCodeEndsWith(String regionCode);
    List<GuavaRegion> findByRegionCodeContainsAndRiName(String regionCode, String riName);
    List<GuavaRegion> findByRegionCodeContainsAndDongName(String regionCode, String dongName);
}
