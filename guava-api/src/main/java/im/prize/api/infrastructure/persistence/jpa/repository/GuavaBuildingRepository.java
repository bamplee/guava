package im.prize.api.infrastructure.persistence.jpa.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GuavaBuildingRepository extends JpaRepository<GuavaBuilding, Long>, JpaSpecificationExecutor<GuavaBuilding> {
    List<GuavaBuilding> findByRegionCodeLikeAndName(String regionCode, String name);

    List<GuavaBuilding> findByRegionCode(String regionCode);

    @Cacheable("findByRegionCodeLike")
    List<GuavaBuilding> findByRegionCodeLike(String regionCode);

    List<GuavaBuilding> findByTypeAndRegionCode(Integer type, String regionCode);

    List<GuavaBuilding> findByAddressLike(String address);

    List<GuavaBuilding> findByPointIsNull();

//    @Cacheable("findByBuildingCode")
//    List<GuavaBuilding> findByBuildingCode(String buildingCode);

    @Query(value = "SELECT * FROM guava_building t WHERE t.building_code = BINARY(?1)", nativeQuery = true)
    Optional<GuavaBuilding> findByBuildingCode(String buildingCode);

    @Query(value = "SELECT * FROM guava_building t WHERE replace(concat(address , name), ' ', '') like ?1 limit 10", nativeQuery = true)
    List<GuavaBuilding> searchByQuery(String query);

    static Specification<GuavaBuilding> search(String name) {
        return (Specification<GuavaBuilding>) ((root, query, builder) -> {
            return builder.like(root.get("name"), "%" + StringUtils.join(name.split(""), "%") + "%");
        });
    }

    static Specification<GuavaBuilding> searchAddress(String address) {
        return (Specification<GuavaBuilding>) ((root, query, builder) -> {
            return builder.like(root.get("address"), "%" + StringUtils.join(address.split(""), "%") + "%");
        });
    }
}
