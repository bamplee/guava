package net.moboo.batch.infrastructure.jpa;

import net.moboo.batch.domain.TradeArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeArticleRepository extends JpaRepository<TradeArticle, Long> {
    List<TradeArticle> findByEndDateIsNull();
    List<TradeArticle> findByRegionCodeStartsWithAndEndDateIsNull(String regionCode);
}
