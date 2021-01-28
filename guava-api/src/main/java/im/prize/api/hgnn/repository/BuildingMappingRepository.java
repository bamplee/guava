package im.prize.api.hgnn.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingMappingRepository extends JpaRepository<BuildingMapping, Long> {
    @Cacheable("findByBuildingCode")
    @Query(value = "SELECT * FROM building_mapping_tb t WHERE t.building_code = BINARY(?1)", nativeQuery = true)
    List<BuildingMapping> findByBuildingCode(String buildingCode);

    @Query(value = "SELECT * FROM building_mapping_tb t WHERE replace(concat(building_name), ' ', '') like ?1 limit 10",
           nativeQuery = true)
    List<BuildingMapping> searchByName(String query);

    @Query(value = "SELECT * FROM building_mapping_tb t WHERE replace(concat(address , building_name), ' ', '') like ?1 limit 10",
           nativeQuery = true)
    List<BuildingMapping> search(String query);

    List<BuildingMapping> findByBuildingCodeIsNull();
}
