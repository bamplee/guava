package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.Region;
import im.prize.api.domain.oboo.TicketRank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRankRepository extends JpaRepository<TicketRank, Long> {

    List<TicketRank> findByRegionAndDate(Region region, String yyyyMmDate);

}
