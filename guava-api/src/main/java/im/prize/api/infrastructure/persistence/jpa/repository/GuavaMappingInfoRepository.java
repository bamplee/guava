package im.prize.api.infrastructure.persistence.jpa.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GuavaMappingInfoRepository extends JpaRepository<GuavaMappingInfo, Long>, JpaSpecificationExecutor<GuavaMappingInfo> {
    List<GuavaMappingInfo> findByIdGreaterThanEqual(Long id);

    Page<GuavaMappingInfo> findByType(Integer type, Pageable pageable);

    List<GuavaMappingInfo> findByAddress(String address);

    List<GuavaMappingInfo> findByRegionCodeStartsWithAndPortalIdIsNotNull(String regionCode);

    List<GuavaMappingInfo> findByBuildingCodeIn(List<String> buildingCodeList);

    List<GuavaMappingInfo> findByRegionCode(String regionCode);

    List<GuavaMappingInfo> findByRegionCodeAndLotNumberAndName(String regionCode, String lotNumber, String name);

    static Specification<GuavaMappingInfo> search(String name) {
        return (Specification<GuavaMappingInfo>) ((root, query, builder) ->
            builder.like(root.get("name"), "%" + StringUtils.join(name.split(""), "%") + "%")
        );
    }
}
