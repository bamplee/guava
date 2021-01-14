package net.moboo.batch.hgnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionTempRepository extends JpaRepository<RegionTemp, Long> {
    Optional<RegionTemp> findByRegionCode(String regionCode);
}
