package net.moboo.batch.infrastructure.jpa;

import net.moboo.batch.domain.MainStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainStatsRepository extends JpaRepository<MainStats, Long> {
    MainStats findByDate(String date);
}
