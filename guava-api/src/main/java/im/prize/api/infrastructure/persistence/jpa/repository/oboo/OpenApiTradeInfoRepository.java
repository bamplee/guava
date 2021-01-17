package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.OpenApiTradeInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface OpenApiTradeInfoRepository extends JpaRepository<OpenApiTradeInfo, Long>, JpaSpecificationExecutor<OpenApiTradeInfo> {

    @Cacheable("findByBuildingIdIsNull")
    List<OpenApiTradeInfo> findByBuildingIdIsNull();
    List<OpenApiTradeInfo> findByBuildingIdIsNull(Pageable pageable);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndAptNameOrderByDateDesc(String dongSigunguCode,
                                                                                     String dongCode,
                                                                                     String aptName,
                                                                                     Pageable pageable);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeOrderByDateDesc(String dongSigunguCode,
                                                                           String dongCode,
                                                                           Pageable pageable);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndDateBetweenOrderByDateDesc(String dongSigunguCode,
                                                                                         String dongCode,
                                                                                         String startDate,
                                                                                         String endDate,
                                                                                         Pageable pageable);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndAreaBetweenOrderByDateDesc(String dongSigunguCode,
                                                                                         String dongCode,
                                                                                         Double startArea,
                                                                                         Double endArea,
                                                                                         Pageable pageable);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndAreaBetweenAndDateBetweenOrderByDateDesc(String dongSigunguCode,
                                                                                                       String dongCode,
                                                                                                       Double startArea,
                                                                                                       Double endArea,
                                                                                                       String startDate,
                                                                                                       String endDate,
                                                                                                       Pageable pageable);

    List<OpenApiTradeInfo> findByDongSigunguCodeOrderByDateDesc(String dongSigunguCode,
                                                                Pageable pageable);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDateBetweenOrderByDateDesc(String dongSigunguCode,
                                                                              String startDate,
                                                                              String endDate,
                                                                              Pageable pageable);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndAreaBetweenOrderByDateDesc(String dongSigunguCode,
                                                                              Double startArea,
                                                                              Double endArea,
                                                                              Pageable pageable);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndAreaBetweenAndDateBetweenOrderByDateDesc(String dongSigunguCode,
                                                                                            Double startArea,
                                                                                            Double endArea,
                                                                                            String startDate,
                                                                                            String endDate,
                                                                                            Pageable pageable);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndAptNameAndDateGreaterThan(String dongSigunguCode,
                                                                                        String dongCode,
                                                                                        String aptName,
                                                                                        String date);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndAptNameAndYear(String dongSigunguCode,
                                                                             String dongCode,
                                                                             String aptName,
                                                                             String year);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndAptNameAndAreaOrderByDateDesc(String dongSigunguCode,
                                                                                            String dongCode,
                                                                                            String aptName,
                                                                                            Double area,
                                                                                            Pageable pageable);

    @Query(value = "select ROUND(AVG(REPLACE(price, ',', '')*1)) as avgPrice, ROUND(MAX(REPLACE(price, ',', '')*1)) as maxPrice, ROUND" +
        "(MIN(REPLACE(price, ',', '')*1)) as minPrice, count(*) as total from openapi_trade_info where dong_sigungu_code = ?1 and " +
        "dong_code = ?2 and year = ?3",
           nativeQuery = true)
    Map<String, Object> averageByDongSigunguCodeAndDongCodeAndYearOrderByDateDesc(String dongSigunguCode,
                                                                                  String dongCode,
                                                                                  String year);

    @Query(value = "select ROUND(AVG(REPLACE(price, ',', '')*1)) as avgPrice, ROUND(MAX(REPLACE(price, ',', '')*1)) as maxPrice, ROUND" +
        "(MIN(REPLACE(price, ',', '')*1)) as minPrice, count(*) as total from openapi_trade_info where dong_sigungu_code = ?1 and " +
        "dong_code = ?2 and apt_name = ?3 and year = ?4",
           nativeQuery = true)
    Map<String, Object> averageByDongSigunguCodeAndDongCodeAndAptNameAndYearOrderByDateDesc(String dongSigunguCode,
                                                                                            String dongCode,
                                                                                            String aptName,
                                                                                            String year);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndAptNameAndDateBetween(String dongSigunguCode,
                                                                                    String dongCode,
                                                                                    String aptName, String startDate, String endDate);

    @Query(value = "select * from openapi_trade_info where dong_sigungu_code = ?1 " +
        "and dong_code = ?2 and apt_name = ?3 order by year desc, month desc, day desc limit 2",
           nativeQuery = true)
    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndAptName(String dongSigunguCode,
                                                                      String dongCode,
                                                                      String aptName);

    List<OpenApiTradeInfo> findByBuildingIdAndYearGreaterThanEqualOrderByDateDesc(String buildingId, String year);

    List<OpenApiTradeInfo> findByBuildingIdAndAreaAndYearGreaterThanEqualOrderByDateDesc(String buildingId, Double area, String year);

    List<OpenApiTradeInfo> findTop100ByBuildingIdOrderByDateDesc(String buildingCode);

    List<OpenApiTradeInfo> findTop100ByBuildingIdAndYearOrderByDateDesc(String buildingCode, String year);

    List<OpenApiTradeInfo> findTop100ByBuildingIdAndAreaBetweenOrderByDateDesc(String buildingCode, Double startArea, Double endArea);

    Page<OpenApiTradeInfo> findByBuildingIdAndDateBetweenOrderByDateDesc(String buildingId,
                                                                         String startDate,
                                                                         String endDate,
                                                                         Pageable pageable);

    Page<OpenApiTradeInfo> findByBuildingIdOrderByDateDesc(String buildingId,
                                                           Pageable pageable);

    Page<OpenApiTradeInfo> findByBuildingIdAndAreaOrderByDateDesc(String buildingId,
                                                                  Double area,
                                                                  Pageable pageable);

    Page<OpenApiTradeInfo> findByBuildingIdAndAreaAndDateBetweenOrderByDateDesc(String buildingId,
                                                                                Double area,
                                                                                String startDate,
                                                                                String endDate,
                                                                                Pageable pageable);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndAptNameAndLotNumberAndBuildingIdIsNull(String sigunguCode,
                                                                                                     String dongCode,
                                                                                                     String name,
                                                                                                     String lotNumber);

    List<OpenApiTradeInfo> findTop100ByDongSigunguCodeAndDongCodeOrderByDateDesc(String dongSigunguCode, String dongCode);

    @Query(value = "SELECT * FROM openapi_trade_info t WHERE t.dong_sigungu_code = ?1 and t.dong_code = ?2 and year = ?3 order by date " +
        "desc limit 100",
           nativeQuery = true)
    List<OpenApiTradeInfo> getTrade(String dongSigunguCode, String dongCode, String year);

    @Cacheable("getMaxPrice")
    @Query(value = "select max(cast(replace(price, ',', '') as unsigned)) from openapi_trade_info where date < ?1 and building_id = ?2 " +
        "and area = ?3",
           nativeQuery = true)
    String getMaxPrice(String date, String buildingId, Double area);

    @Cacheable("getMaxPriceByAreahundredthsDecimal")
    @Query(value = "select max(cast(replace(price, ',', '') as unsigned)) from openapi_trade_info where date < ?1 and building_id = ?2 " +
        "and round(area, 2) = round(?3, 2)",
           nativeQuery = true)
    String getMaxPriceByAreahundredthsDecimal(String date, String buildingId, Double area);

    @Cacheable("getMaxPriceByAreaTenthsDecimal")
    @Query(value = "select max(cast(replace(price, ',', '') as unsigned)) from openapi_trade_info where date < ?1 and building_id = ?2 " +
        "and round(area, 1) = round(?3, 1)",
           nativeQuery = true)
    String getMaxPriceByAreaTenthsDecimal(String date, String buildingId, Double area);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndYearGreaterThanOrderByDateDesc(String sigunguCode,
                                                                                             String dongCode,
                                                                                             String year);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndDongCodeAndAreaBetweenAndYearGreaterThanEqualOrderByDateDesc(String sigunguCode,
                                                                                                                String dongCode,
                                                                                                                Double startArea,
                                                                                                                Double endArea,
                                                                                                                String year);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndYearGreaterThanOrderByDateDesc(String sigunguCode, String baseYear);

    List<OpenApiTradeInfo> findByDongSigunguCodeAndAreaBetweenAndYearGreaterThanEqualOrderByDateDesc(String sigunguCode,
                                                                                                     Double startArea,
                                                                                                     Double endArea,
                                                                                                     String baseYear);
}
