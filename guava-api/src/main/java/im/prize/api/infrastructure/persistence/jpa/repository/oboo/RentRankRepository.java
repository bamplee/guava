package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.Region;
import im.prize.api.domain.oboo.RentRank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentRankRepository extends JpaRepository<RentRank, Long> {
    List<RentRank> findByRegionAndDate(Region region, String yyyyMmDate);
}
