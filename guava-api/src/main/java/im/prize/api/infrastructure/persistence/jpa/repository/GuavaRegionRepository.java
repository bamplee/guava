package im.prize.api.infrastructure.persistence.jpa.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface GuavaRegionRepository extends JpaRepository<GuavaRegion, Long>, JpaSpecificationExecutor<GuavaRegion> {
    @Cacheable("findByRegionCode")
    Optional<GuavaRegion> findByRegionCode(String regionCode);

    @Cacheable("findBySidoAndSigunguAndSigunguNameAndIsActive")
    Optional<GuavaRegion> findBySidoAndSigunguAndSigunguNameAndIsActive(String sido, String sigungu, String sigunguName, Boolean isActive);

    @Cacheable("findBySidoAndSigunguAndSigunguName")
    Optional<GuavaRegion> findBySidoAndSigunguAndSigunguName(String sido, String sigungu, String sigunguName);

    @Cacheable("findBySidoAndSigunguAndDongNameAndAndRiNameIsActive")
    Optional<GuavaRegion> findBySidoAndSigunguAndDongNameAndRiNameAndIsActive(String sido, String sigungu, String dongName, String riName, Boolean isActive);

    @Cacheable("findBySidoAndSigunguAndDongNameAndRiName")
    Optional<GuavaRegion> findBySidoAndSigunguAndDongNameAndRiName(String sido, String sigungu, String dongName, String riName);

    @Cacheable("findBySidoAndSigunguAndDongNameAndIsActive")
    Optional<GuavaRegion> findBySidoAndSigunguAndDongNameAndIsActive(String sido, String sigungu, String dongName, Boolean isActive);

    @Cacheable("findBySidoAndSigunguAndDongName")
    Optional<GuavaRegion> findBySidoAndSigunguAndDongName(String sido, String sigungu, String dongName);

    @Cacheable("findBySidoAndSigunguAndSigunguNameAndDongName")
    Optional<GuavaRegion> findBySidoAndSigunguAndSigunguNameAndDongName(String sido, String sigungu, String sigunguName, String dongName);

    @Cacheable("findBySidoAndSigunguAndRiNameAndIsActive")
    Optional<GuavaRegion> findBySidoAndSigunguAndRiNameAndIsActive(String sido, String sigungu, String riName, Boolean isActive);

    @Cacheable("findBySidoAndSigunguAndRiName")
    Optional<GuavaRegion> findBySidoAndSigunguAndRiName(String sido, String sigungu, String riName);

    @Cacheable("findBySidoAndSigunguAndNameLike")
    Optional<GuavaRegion> findBySidoAndSigunguAndNameContains(String sido, String sigungu, String name);

    List<GuavaRegion> findByRegionCodeLike(String regionCode);
    List<GuavaRegion> findByRegionCodeLikeAndIsActiveIsNotNull(String regionCode);

    List<GuavaRegion> findBySido(String sidoCode);

    List<GuavaRegion> findBySigungu(String sigunguCode);

    List<GuavaRegion> findByDong(String dongCode);

    List<GuavaRegion> findByRi(String riCode);

    Optional<GuavaRegion> findByLngAndLat(Double lng, Double lat);

    static Specification<GuavaRegion> search(String name) {
        return (Specification<GuavaRegion>) ((root, query, builder) ->
            builder.like(root.get("name"), "%" + StringUtils.join(name.split(""), "%") + "%")
        );
    }
}
