package im.prize.api.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import im.prize.api.interfaces.GuavaTradeSearch;
import im.prize.api.interfaces.response.AreaResponse;
import im.prize.api.interfaces.response.GuavaTradeResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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
    private final ObjectMapper objectMapper;

    public static final DateTimeFormatter DATE_TIME_FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public GuavaTradeServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                 GuavaBuildingRepository guavaBuildingRepository,
                                 OpenApiTradeInfoRepository openApiTradeInfoRepository,
                                 GuavaBuildingAreaRepository guavaBuildingAreaRepository,
                                 BuildingMappingRepository buildingMappingRepository,
                                 TradeSummaryRepository tradeSummaryRepository, ObjectMapper objectMapper) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.openApiTradeInfoRepository = openApiTradeInfoRepository;
        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
        this.buildingMappingRepository = buildingMappingRepository;
        this.tradeSummaryRepository = tradeSummaryRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<GuavaTradeResponse> getRegionTrade(String regionId, Integer page, Integer startArea, Integer endArea, String date) {
        Optional<GuavaRegion> optionalGuavaRegion = guavaRegionRepository.findById(Long.valueOf(regionId));
        if (optionalGuavaRegion.isPresent()) {
            // todo
            GuavaRegion guavaRegion = optionalGuavaRegion.get();
            Page<TradeSummary> tradeSummaryPage = tradeSummaryRepository.findAll(this.getParams(guavaRegion.getValidRegionCode(),
                                                                                                null,
                                                                                                null,
                                                                                                date),
                                                                                 this.getPage(page));
            return tradeSummaryPage.getContent()
                                   .stream()
                                   .map(GuavaTradeResponse::transform)
//                                   // fixme building id값 조회 안하도록
//                                   .peek(x -> x.setBuildingId(String.valueOf(buildingMappingRepository.findByBuildingCode(x.getBuildingId())
//                                                                                                      .stream()
//                                                                                                      .findFirst()
//                                                                                                      .map(BuildingMapping::getId)
//                                                                                                      .orElse(0l))))
                                   .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    @Override
    public List<GuavaTradeResponse> getBuildingTradeList(String buildingId, Integer page, String areaId, String date) {
        Optional<BuildingMapping> optionalBuildingMapping = buildingMappingRepository.findById(Long.valueOf(buildingId));
        if (optionalBuildingMapping.isPresent()) {
            BuildingMapping buildingMapping = optionalBuildingMapping.get();
            Page<TradeSummary> tradeSummaryPage = tradeSummaryRepository.findAll(this.getParams(null,
                                                                                                buildingMapping.getBuildingCode(),
                                                                                                areaId,
                                                                                                date),
                                                                                 this.getPage(page));
            return tradeSummaryPage.getContent()
                                   .stream()
                                   .map(GuavaTradeResponse::transform)
//                                   // fixme building id값 조회 안하도록
//                                   .peek(x -> x.setBuildingId(buildingId))
                                   .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private PageRequest getPage(Integer page) {
        return PageRequest.of(page, 100, Sort.by(Sort.Direction.DESC, "date"));
    }

    private Specification<TradeSummary> getParams(String regionCode, String buildingCode, String areaCode, String date) {
        Map<String, Object> paramsMap = objectMapper.convertValue(GuavaTradeSearch.builder()
                                                                                  .regionCode(regionCode)
                                                                                  .buildingCode(buildingCode)
                                                                                  .areaCode(areaCode)
                                                                                  .date(date)
                                                                                  .build(), Map.class);
        Map<TradeSummarySpecs.SearchKey, Object> params = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            if (ObjectUtils.isNotEmpty(entry.getValue()) && StringUtils.isNotEmpty(String.valueOf(entry.getValue()))) {
                params.put(TradeSummarySpecs.SearchKey.convert(entry.getKey()), entry.getValue());
            }
        }
        return TradeSummarySpecs.searchWith(params);
    }

    private GuavaTradeResponse transform(OpenApiTradeInfo openApiTradeInfo) {
        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findByBuildingCode(openApiTradeInfo.getBuildingId());
        LocalDate yyyyMMdd = LocalDate.parse(openApiTradeInfo.getDate(), DATE_TIME_FORMATTER_YYYYMMDD);
//        String beforeHighPrice = openApiTradeInfoRepository.getMaxPrice(yyyyMMdd.format(DATE_TIME_FORMATTER_YYYYMMDD),
//                                                                        openApiTradeInfo.getBuildingId(),
//                                                                        openApiTradeInfo.getArea());
//        String beforeHighPrice = openApiTradeInfoRepository.getMaxPrice(LocalDate.now().format(DATE_TIME_FORMATTER_YYYYMMDD),
//                                                                        openApiTradeInfo.getBuildingId(),
//                                                                        openApiTradeInfo.getArea());
////        boolean isHighPrice = false;
////        if (StringUtils.isNotEmpty(beforeHighPrice)) {
////            isHighPrice = Integer.valueOf(openApiTradeInfo.getPrice().replace(",", "")) >= Integer.valueOf(
////                beforeHighPrice.replace(",", ""));
////            beforeHighPrice = GuavaUtils.getSummaryPrice(beforeHighPrice);
////        } else {
////            beforeHighPrice = "거래없음";
////        }

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
//                                 .beforeMaxPrice(beforeHighPrice)
//                                 .isHighPrice(isHighPrice)
                                 .isNew(openApiTradeInfo.getCreatedDateTime().toLocalDate().equals(LocalDate.now()))
                                 .build();
    }
}
