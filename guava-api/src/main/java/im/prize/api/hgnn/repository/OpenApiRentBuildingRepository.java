package im.prize.api.hgnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenApiRentBuildingRepository extends JpaRepository<OpenApiRentBuilding, Long> {
    //    List<OpenApiRentBuilding> findByTypeIsNull();
    List<OpenApiRentBuilding> findByRegionCodeIsNull();
    List<OpenApiRentBuilding> findByRegionCodeIsNotNull();
}
