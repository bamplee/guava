package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.Region;
import im.prize.api.domain.oboo.TradeRanks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRanksRepository extends JpaRepository<TradeRanks, Long> {
    List<TradeRanks> findByRegionAndDate(Region region, String date);
}
