package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.RegionTemp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionTempRepository extends JpaRepository<RegionTemp, Long> {
    Optional<RegionTemp> findByRegionCode(String regionCode);
}
