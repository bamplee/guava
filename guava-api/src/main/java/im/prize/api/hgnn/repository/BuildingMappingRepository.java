package im.prize.api.hgnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingMappingRepository extends JpaRepository<BuildingMapping, Long> {
}
