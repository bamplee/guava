package im.prize.api.application;

import com.google.common.collect.Lists;
import im.prize.api.domain.oboo.OpenApiTradeInfo;
import im.prize.api.hgnn.repository.GuavaBuildingAreaRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingArea;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionStatsRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.OpenApiTradeInfoRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.TradeArticleRepository;
import im.prize.api.interfaces.response.GuavaChartResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GuavaChartServiceImpl implements GuavaChartService {
    @Value("${app.kakao.apiKey}")
    private String kakaoMapApiKey;
    private final GuavaRegionRepository guavaRegionRepository;
    private final GuavaBuildingRepository guavaBuildingRepository;
    private final EntityManager entityManager;
    private final OpenApiTradeInfoRepository openApiTradeInfoRepository;
    private final TradeArticleRepository tradeArticleRepository;
    private final GuavaRegionStatsRepository guavaRegionStatsRepository;
    private final GuavaBuildingAreaRepository guavaBuildingAreaRepository;

    public static final DateTimeFormatter DATE_TIME_FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public GuavaChartServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                 GuavaBuildingRepository guavaBuildingRepository,
                                 EntityManager entityManager,
                                 OpenApiTradeInfoRepository openApiTradeInfoRepository,
                                 TradeArticleRepository tradeArticleRepository,
                                 GuavaRegionStatsRepository guavaRegionStatsRepository,
                                 GuavaBuildingAreaRepository guavaBuildingAreaRepository) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.entityManager = entityManager;
        this.openApiTradeInfoRepository = openApiTradeInfoRepository;
        this.tradeArticleRepository = tradeArticleRepository;
        this.guavaRegionStatsRepository = guavaRegionStatsRepository;
        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
    }

    @Override
    public List<GuavaChartResponse> getRegionChartList(String regionId, Integer startArea, Integer endArea, Long beforeMonth) {
        Optional<GuavaRegion> byId = guavaRegionRepository.findById(Long.valueOf(regionId));
        if (!byId.isPresent()) {
            return Lists.newArrayList();
        }
        GuavaRegion guavaRegion = byId.get();
        List<OpenApiTradeInfo> openApiTradeInfoList = Lists.newArrayList();
        String baseYear = String.valueOf(YearMonth.now().minusMonths(beforeMonth).getYear());
        if (guavaRegion.getRegionType() == RegionType.DONG) {
            if (startArea + endArea == 0) {
                openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndYearGreaterThanOrderByDateDesc(
                    guavaRegion.getSigunguCode(),
                    guavaRegion.getDongCode(),
                    baseYear);
            } else {
                openApiTradeInfoList =
                    openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAreaBetweenAndYearGreaterThanEqualOrderByDateDesc(
                        guavaRegion.getSigunguCode(),
                        guavaRegion.getDongCode(),
                        Double.valueOf(startArea),
                        Double.valueOf(endArea),
                        baseYear);
            }
        } else if (guavaRegion.getRegionType() == RegionType.SIGUNGU) {
            if (startArea + endArea == 0) {
                openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeAndYearGreaterThanOrderByDateDesc(
                    guavaRegion.getSigunguCode(),
                    baseYear);
            } else {
                openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeAndAreaBetweenAndYearGreaterThanEqualOrderByDateDesc(
                    guavaRegion.getSigunguCode(),
                    Double.valueOf(startArea),
                    Double.valueOf(endArea),
                    baseYear);
            }
        }
        return openApiTradeInfoList.stream()
                                   .filter(x -> StringUtils.isNotEmpty(x.getBuildingId()))
                                   .map(this::transformChartData)
                                   .collect(Collectors.toList());
    }

    @Override
    public List<GuavaChartResponse> getChartList(String buildingCode, String areaId, Long sinceYear) {
        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findById(Long.valueOf(buildingCode));
        List<OpenApiTradeInfo> infos = Lists.newArrayList();
        String baseYear = String.valueOf(YearMonth.now().minusMonths(sinceYear).getYear());
        if (optionalGuavaBuilding.isPresent()) {
            GuavaBuilding guavaBuilding = optionalGuavaBuilding.get();
            Optional<GuavaBuildingArea> optionalGuavaBuildingArea = Optional.empty();
            if (StringUtils.isNotEmpty(areaId)) {
                optionalGuavaBuildingArea = this.getArea(areaId);
                if (optionalGuavaBuildingArea.isPresent()) {
                    infos =
                        openApiTradeInfoRepository.findByBuildingIdAndAreaAndYearGreaterThanEqualOrderByDateDesc(guavaBuilding.getBuildingCode(),
                                                                                                                 optionalGuavaBuildingArea
                                                                                                                     .get()
                                                                                                                     .getPrivateArea(),
                                                                                                                 baseYear);
                }
            }

            if (infos.size() == 0) {
                infos = openApiTradeInfoRepository.findByBuildingIdAndYearGreaterThanEqualOrderByDateDesc(guavaBuilding.getBuildingCode(),
                                                                                                          baseYear);
            }
        }
        return infos.stream().map(this::transformChartData).collect(Collectors.toList());
    }

    public Optional<GuavaBuildingArea> getArea(String areaId) {
        return guavaBuildingAreaRepository.findById(Long.valueOf(areaId));
    }

    private GuavaChartResponse transformChartData(OpenApiTradeInfo openApiTradeInfo) {
        return GuavaChartResponse.builder()
                                 .date(openApiTradeInfo.getDate())
                                 .area(String.valueOf(openApiTradeInfo.getArea()))
//                                 .floor(openApiTradeInfo.getFloor())
                                 .price(openApiTradeInfo.getPrice().replace(",", ""))
                                 .build();
    }
}
