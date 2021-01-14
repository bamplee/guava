package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.TradeItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TradeItemRepository extends JpaRepository<TradeItem, Long> {
    List<TradeItem> findByStartDateBeforeAndEndDateIsNullAndHcpcNo(String startDate, String hcpcNo);
    List<TradeItem> findByEndDateIsNull();
    List<TradeItem> findByHcpcNo(String hcpcNo);
    List<TradeItem> findByHcpcNoAndSpc2OrderByCfmYmd(String hcpcNo, String spc2);

    List<TradeItem> findByHcpcNo(String hcpcNo, Pageable pageable);

    @Query(value = "select * from trade_item where hcpc_no = ?1 order by id desc limit 1",
           nativeQuery = true)
    Optional<TradeItem> findRecentlyPrice(String hcpcNo);
}
