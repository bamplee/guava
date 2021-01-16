package im.prize.api.hgnn.repository;

import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuavaBuildingAreaRepository extends JpaRepository<GuavaBuildingArea, Long> {
    List<GuavaBuildingArea> findByBuildingCode(String buildingCode);
}
