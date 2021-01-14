package net.moboo.batch.infrastructure.jpa;

import net.moboo.batch.domain.OpenApiTradeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OpenApiTradeInfoRepository extends JpaRepository<OpenApiTradeInfo, Long> {
    List<OpenApiTradeInfo> findByYearAndMonth(String year, String month);

//    List<OpenApiTradeInfo> findByYearAndDongSigunguCodeOrderByDateDesc(String year, String dongSigunguCode);

//    List<OpenApiTradeInfo> findByYearAndDongSigunguCodeAndDongCodeOrderByDateDesc(String year, String dongSigunguCode, String dongCode);

//    List<OpenApiTradeInfo> findTop100ByDongSigunguCodeAndDongCodeOrderByDateDesc(String dongSigunguCode, String dongCode);

    @Query(value = "SELECT * FROM openapi_trade_info t WHERE t.year = ?1 and t.dong_sigungu_code = ?2 and t.dong_code = ?3 order by date desc limit 30", nativeQuery = true)
    List<OpenApiTradeInfo> getTrade(String year, String dongSigunguCode, String dongCode);

//    List<OpenApiTradeInfo> findTop50ByDongSigunguCodeOrderByDateDesc(String dongSigunguCode);
}
