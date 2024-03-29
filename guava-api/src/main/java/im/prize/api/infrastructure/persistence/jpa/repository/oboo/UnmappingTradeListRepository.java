package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnmappingTradeListRepository extends JpaRepository<UnmappingTradeList, Long> {
    Page<UnmappingTradeList> findByBuildingIdIsNull(Pageable pageable);
}
