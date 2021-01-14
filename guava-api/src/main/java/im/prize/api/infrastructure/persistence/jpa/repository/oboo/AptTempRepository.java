package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.AptTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AptTempRepository extends JpaRepository<AptTemp, Long> {
    List<AptTemp> findByRegionCode(String regionCode);
    List<AptTemp> findByAptId(String aptId);
}
