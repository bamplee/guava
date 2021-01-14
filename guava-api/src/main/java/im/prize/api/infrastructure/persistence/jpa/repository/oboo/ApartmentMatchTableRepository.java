package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.ApartmentMatchTable;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApartmentMatchTableRepository extends JpaRepository<ApartmentMatchTable, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO apartment_match_table SET id = ?1, location = ST_GEOMFROMTEXT(?2)")
    int savePoint(Long id, Point location);

    List<ApartmentMatchTable> findByHgnnIdIsNull();
    List<ApartmentMatchTable> findByPortalIdIsNotNull();

    List<ApartmentMatchTable> findByHgnnRegionCodeIsNotNullAndLocationIsNull();

    List<ApartmentMatchTable> findByHgnnRegionCodeStartsWithAndPortalIdIsNotNull(String regionCode);

    List<ApartmentMatchTable> findByPortalId(String portalId);

    List<ApartmentMatchTable> findByDongSigunguCode(String dongSigunguCode);

    ApartmentMatchTable findByDongSigunguCodeAndDongCodeAndLotNumber(String dongSigunguCode, String dongCode, String lotNumber);
}
