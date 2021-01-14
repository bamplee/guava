package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.MainStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainStatsRepository extends JpaRepository<MainStats, Long> {
    MainStats findByDate(String date);
}
