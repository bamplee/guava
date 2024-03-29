package net.moboo.batch.hgnn.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GuavaBuildingRepository extends JpaRepository<GuavaBuilding, Long> {
    List<GuavaBuilding> findByRegionCode(String regionCode);

    List<GuavaBuilding> findByRegionCodeStartsWith(String regionCode);

    List<GuavaBuilding> findByAddress(String address);

    @Cacheable("findByAddressContaining")
    List<GuavaBuilding> findByAddressContaining(String address);

    List<GuavaBuilding> findByRegionCodeStartsWithAndPortalIdIsNotNull(String regionCode);
}
