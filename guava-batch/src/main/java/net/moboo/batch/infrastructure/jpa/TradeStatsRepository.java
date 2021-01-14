package net.moboo.batch.infrastructure.jpa;

import net.moboo.batch.domain.TradeStats;
import net.moboo.batch.domain.TradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TradeStatsRepository extends JpaRepository<TradeStats, Long> {
    List<TradeStats> findByDateAndRegionCodeAndTradeType(String yyyyMmDate, String code, TradeType tradeType);

    @Modifying
    @Query("delete from TradeStats t where t.date = :date and t.regionCode = :regionCode and t.tradeType = :tradeType")
    @Transactional
    void deleteByDateAndRegionCodeAndTradeType(@Param("date") String date,
                                               @Param("regionCode") String regionCode,
                                               @Param("tradeType") TradeType tradeType);
}
