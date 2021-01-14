package im.prize.api.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GuavaRegionPriceRepository extends JpaRepository<GuavaRegionPrice, Long>, JpaSpecificationExecutor<GuavaRegion> {
    Optional<GuavaRegionPrice> findByRegionCode(String regionCode);
}
