package net.moboo.batch.infrastructure.jpa;

import net.moboo.batch.domain.TradeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradeItemRepository extends JpaRepository<TradeItem, Long> {
    List<TradeItem> findByEndDateIsNull();

    Optional<TradeItem> findByAtclNo(String atclNo);

    List<TradeItem> findByHcpcNo(String hcpcNo);

    List<TradeItem> findByHcpcNoIn(List<String> hcpcNo);
}
