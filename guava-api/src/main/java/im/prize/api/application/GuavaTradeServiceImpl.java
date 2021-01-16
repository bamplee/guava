package im.prize.api.application;

import com.google.common.collect.Lists;
import im.prize.api.domain.oboo.OpenApiTradeInfo;
import im.prize.api.hgnn.repository.BuildingMapping;
import im.prize.api.hgnn.repository.BuildingMappingRepository;
import im.prize.api.hgnn.repository.GuavaBuildingAreaRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingArea;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.OpenApiTradeInfoRepository;
import im.prize.api.interfaces.response.AreaResponse;
import im.prize.api.interfaces.response.GuavaTradeResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GuavaTradeServiceImpl implements GuavaTradeService {
    @Value("${app.kakao.apiKey}")
    private String kakaoMapApiKey;
    private final GuavaRegionRepository guavaRegionRepository;
    private final GuavaBuildingRepository guavaBuildingRepository;
    private final OpenApiTradeInfoRepository openApiTradeInfoRepository;
    private final GuavaBuildingAreaRepository guavaBuildingAreaRepository;
    private final BuildingMappingRepository buildingMappingRepository;
    private final TradeSummaryRepository tradeSummaryRepository;

    public static final DateTimeFormatter DATE_TIME_FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public GuavaTradeServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                 GuavaBuildingRepository guavaBuildingRepository,
                                 OpenApiTradeInfoRepository openApiTradeInfoRepository,
                                 GuavaBuildingAreaRepository guavaBuildingAreaRepository,
                                 BuildingMappingRepository buildingMappingRepository) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.openApiTradeInfoRepository = openApiTradeInfoRepository;
        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
        this.buildingMappingRepository = buildingMappingRepository;
    }

    @Override
    public List<GuavaTradeResponse> getRegionTrade(String regionId, Integer page, Integer startArea, Integer endArea, String date) {
        Optional<GuavaRegion> byId = guavaRegionRepository.findById(Long.valueOf(regionId));
        if (!byId.isPresent()) {
            return Lists.newArrayList();
        }
        GuavaRegion guavaRegion = byId.get();
        List<OpenApiTradeInfo> openApiTradeInfoList = Lists.newArrayList();
        if (guavaRegion.getRegionType() == RegionType.DONG) {
            if (startArea + endArea == 0) {
                if (StringUtils.isNotEmpty(date)) {
                    openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndDateBetweenOrderByDateDesc(
                        guavaRegion.getSigunguCode(),
                        guavaRegion.getDongCode(),
                        date + "01",
                        date + "31",
                        PageRequest.of(page, 100));
                } else {
                    openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeOrderByDateDesc(
                        guavaRegion.getSigunguCode(),
                        guavaRegion.getDongCode(),
                        PageRequest.of(page, 100));
                }
            } else {
                if (StringUtils.isNotEmpty(date)) {
                    openApiTradeInfoList =
                        openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAreaBetweenAndDateBetweenOrderByDateDesc(
                            guavaRegion.getSigunguCode(),
                            guavaRegion.getDongCode(),
                            Double.valueOf(startArea),
                            Double.valueOf(endArea),
                            date + "01",
                            date + "31",
                            PageRequest.of(page, 100));
                } else {
                    openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAreaBetweenOrderByDateDesc(
                        guavaRegion.getSigunguCode(),
                        guavaRegion.getDongCode(),
                        Double.valueOf(startArea),
                        Double.valueOf(endArea),
                        PageRequest.of(page, 100));
                }
            }
        } else if (guavaRegion.getRegionType() == RegionType.SIGUNGU) {
            if (startArea + endArea == 0) {
                if (StringUtils.isNotEmpty(date)) {
                    openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeAndDateBetweenOrderByDateDesc(
                        guavaRegion.getSigunguCode(),
                        date + "01",
                        date + "31",
                        PageRequest.of(page, 100));
                } else {
                    openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeOrderByDateDesc(
                        guavaRegion.getSigunguCode(),
                        PageRequest.of(page, 100));
                }
            } else {
                if (StringUtils.isNotEmpty(date)) {
                    openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeAndAreaBetweenAndDateBetweenOrderByDateDesc(
                        guavaRegion.getSigunguCode(),
                        Double.valueOf(startArea),
                        Double.valueOf(endArea),
                        date + "01",
                        date + "31",
                        PageRequest.of(page, 100));
                } else {
                    openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeAndAreaBetweenOrderByDateDesc(
                        guavaRegion.getSigunguCode(),
                        Double.valueOf(startArea),
                        Double.valueOf(endArea),
                        PageRequest.of(page, 100));
                }
            }
        }
        return openApiTradeInfoList.stream()
                                   .filter(x -> StringUtils.isNotEmpty(x.getBuildingId()))
                                   .map(this::transform)
                                   .collect(Collectors.toList());
    }

    @Override
    public List<GuavaTradeResponse> getBuildingTradeList(String buildingId, Integer page, String areaId, String date) {
        PageRequest pageRequest = PageRequest.of(page, 100);
        Optional<BuildingMapping> optionalBuildingMapping = buildingMappingRepository.findById(Long.valueOf(buildingId));
        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findByBuildingCode(optionalBuildingMapping.get().getBuildingCode());
        if (!optionalGuavaBuilding.isPresent()) {
            return Lists.newArrayList();
        }
        GuavaBuilding guavaBuilding = optionalGuavaBuilding.get();
        String buildingCode = guavaBuilding.getBuildingCode();
        Page<OpenApiTradeInfo> byBuildingIdOrderByDateDesc = Page.empty();
        Optional<GuavaBuildingArea> optionalArea = Optional.empty();
        if (StringUtils.isNotEmpty(areaId)) {
            optionalArea = guavaBuildingAreaRepository.findById(Long.valueOf(areaId));
            if (optionalArea.isPresent()) {
                if (StringUtils.isNotEmpty(date)) {
                    byBuildingIdOrderByDateDesc = openApiTradeInfoRepository.findByBuildingIdAndAreaAndDateBetweenOrderByDateDesc(
                        buildingCode,
                        optionalArea.get()
                                    .getPrivateArea(),
                        date + "01",
                        date + "31",
                        pageRequest);
                } else {
                    byBuildingIdOrderByDateDesc = openApiTradeInfoRepository.findByBuildingIdAndAreaOrderByDateDesc(buildingCode,
                                                                                                                    optionalArea.get()
                                                                                                                                .getPrivateArea(),
                                                                                                                    pageRequest);
                }
            } else {
                if (StringUtils.isNotEmpty(date)) {
                    byBuildingIdOrderByDateDesc = openApiTradeInfoRepository.findByBuildingIdAndDateBetweenOrderByDateDesc(buildingCode,
                                                                                                                           date + "01",
                                                                                                                           date + "31",
                                                                                                                           pageRequest);
                } else {
                    byBuildingIdOrderByDateDesc = openApiTradeInfoRepository.findByBuildingIdOrderByDateDesc(buildingCode,
                                                                                                             pageRequest);
                }
            }
        } else {
            if (StringUtils.isNotEmpty(date)) {
                byBuildingIdOrderByDateDesc = openApiTradeInfoRepository.findByBuildingIdAndDateBetweenOrderByDateDesc(buildingCode,
                                                                                                                       date + "01",
                                                                                                                       date + "31",
                                                                                                                       pageRequest);
            } else {
                byBuildingIdOrderByDateDesc = openApiTradeInfoRepository.findByBuildingIdOrderByDateDesc(buildingCode,
                                                                                                         pageRequest);
            }
        }

        return byBuildingIdOrderByDateDesc.getContent().stream().map(this::transform).collect(Collectors.toList());
    }

    private GuavaTradeResponse transform(OpenApiTradeInfo openApiTradeInfo) {
        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findByBuildingCode(openApiTradeInfo.getBuildingId());
        LocalDate yyyyMMdd = LocalDate.parse(openApiTradeInfo.getDate(), DATE_TIME_FORMATTER_YYYYMMDD);
//        String beforeHighPrice = openApiTradeInfoRepository.getMaxPrice(yyyyMMdd.format(DATE_TIME_FORMATTER_YYYYMMDD),
//                                                                        openApiTradeInfo.getBuildingId(),
//                                                                        openApiTradeInfo.getArea());
        String beforeHighPrice = openApiTradeInfoRepository.getMaxPrice(LocalDate.now().format(DATE_TIME_FORMATTER_YYYYMMDD),
                                                                        openApiTradeInfo.getBuildingId(),
                                                                        openApiTradeInfo.getArea());
        boolean isHighPrice = false;
        if (StringUtils.isNotEmpty(beforeHighPrice)) {
            isHighPrice = Integer.valueOf(openApiTradeInfo.getPrice().replace(",", "")) >= Integer.valueOf(
                beforeHighPrice.replace(",", ""));
            beforeHighPrice = GuavaUtils.getSummaryPrice(beforeHighPrice);
        } else {
            beforeHighPrice = "거래없음";
        }

        GuavaBuildingArea guavaBuildingArea = GuavaUtils.getAreaByPrivateArea(optionalGuavaBuilding.get().getAreaList(),
                                                                              String.valueOf(openApiTradeInfo.getArea()));
        Optional<AreaResponse> areaResponse = AreaResponse.transform(guavaBuildingArea);
        if (!areaResponse.isPresent()) {
            areaResponse = Optional.of(AreaResponse.builder()
                                                   .publicArea(String.valueOf(openApiTradeInfo.getArea()))
                                                   .privateArea(String.valueOf(openApiTradeInfo.getArea()))
                                                   .build());
        }
        return GuavaTradeResponse.builder()
                                 .regionId(String.valueOf(guavaRegionRepository.findByRegionCode(openApiTradeInfo.getRegionCode()).map(
                                     GuavaRegion::getId).orElse(1L)))
                                 .buildingId(String.valueOf(optionalGuavaBuilding.map(GuavaBuilding::getId).orElse(0L)))
                                 .type("TRADE")
                                 .name(openApiTradeInfo.getAptName())
                                 .address(openApiTradeInfo.getDong())
                                 .date(yyyyMMdd.format(DateTimeFormatter.ofPattern("yy.MM.dd")))
                                 .year(yyyyMMdd.format(DateTimeFormatter.ofPattern("yyyy")))
                                 .month(yyyyMMdd.format(DateTimeFormatter.ofPattern("MM")))
                                 .day(yyyyMMdd.format(DateTimeFormatter.ofPattern("dd")))
                                 .floor(openApiTradeInfo.getFloor())
                                 .price(openApiTradeInfo.getTradePrice())
                                 .area(areaResponse.orElse(AreaResponse.builder().build()))
                                 .beforeMaxPrice(beforeHighPrice)
                                 .isHighPrice(isHighPrice)
                                 .isNew(openApiTradeInfo.getCreatedDateTime().toLocalDate().equals(LocalDate.now()))
                                 .build();
    }
}
