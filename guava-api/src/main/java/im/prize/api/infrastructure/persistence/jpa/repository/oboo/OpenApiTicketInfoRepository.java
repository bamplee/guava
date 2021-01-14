package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.OpenApiTicketInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpenApiTicketInfoRepository extends JpaRepository<OpenApiTicketInfo, Long> {
    List<OpenApiTicketInfo> findByYearAndMonth(String year, String month);
}
