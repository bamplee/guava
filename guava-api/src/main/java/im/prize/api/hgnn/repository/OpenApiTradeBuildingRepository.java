package im.prize.api.hgnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenApiTradeBuildingRepository extends JpaRepository<OpenApiTradeBuilding, Long> {
    List<OpenApiTradeBuilding> findByRegionCodeIsNull();
    List<OpenApiTradeBuilding> findByRegionCodeIsNotNull();
}
