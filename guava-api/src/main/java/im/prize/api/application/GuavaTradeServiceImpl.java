package im.prize.api.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import im.prize.api.domain.oboo.OpenApiRentInfo;
import im.prize.api.domain.oboo.OpenApiTradeInfo;
import im.prize.api.hgnn.repository.BuildingMapping;
import im.prize.api.hgnn.repository.BuildingMappingRepository;
import im.prize.api.hgnn.repository.GuavaBuildingAreaRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingArea;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.OpenApiRentInfoRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.OpenApiTradeInfoRepository;
import im.prize.api.interfaces.GuavaTradeSearch;
import im.prize.api.interfaces.response.GuavaTradeResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GuavaTradeServiceImpl implements GuavaTradeService {
//    private static final Integer PAGE_SIZE = 30;
    @Value("${app.kakao.apiKey}")
    private String kakaoMapApiKey;
    private final GuavaRegionRepository guavaRegionRepository;
    private final GuavaBuildingRepository guavaBuildingRepository;
    private final OpenApiTradeInfoRepository openApiTradeInfoRepository;
    private final OpenApiRentInfoRepository openApiRentInfoRepository;
    private final GuavaBuildingAreaRepository guavaBuildingAreaRepository;
    private final BuildingMappingRepository buildingMappingRepository;
    private final TradeSummaryRepository tradeSummaryRepository;
    private final RentSummaryRepository rentSummaryRepository;
    private final ObjectMapper objectMapper;

    public static final DateTimeFormatter DATE_TIME_FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public GuavaTradeServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                 GuavaBuildingRepository guavaBuildingRepository,
                                 OpenApiTradeInfoRepository openApiTradeInfoRepository,
                                 OpenApiRentInfoRepository openApiRentInfoRepository,
                                 GuavaBuildingAreaRepository guavaBuildingAreaRepository,
                                 BuildingMappingRepository buildingMappingRepository,
                                 TradeSummaryRepository tradeSummaryRepository,
                                 RentSummaryRepository rentSummaryRepository,
                                 ObjectMapper objectMapper) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.openApiTradeInfoRepository = openApiTradeInfoRepository;
        this.openApiRentInfoRepository = openApiRentInfoRepository;
        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
        this.buildingMappingRepository = buildingMappingRepository;
        this.tradeSummaryRepository = tradeSummaryRepository;
        this.rentSummaryRepository = rentSummaryRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<GuavaTradeResponse> getRegionTrade(String tradeType,
                                                   String regionId,
                                                   Integer page,
                                                   Integer size,
                                                   Integer startArea,
                                                   Integer endArea,
                                                   String date) {
        Optional<GuavaRegion> optionalGuavaRegion = guavaRegionRepository.findById(Long.valueOf(regionId));
        if (optionalGuavaRegion.isPresent()) {
            // todo
            GuavaRegion guavaRegion = optionalGuavaRegion.get();
            if ("trade".equals(tradeType)) {
                return tradeSummaryRepository.findAll(this.getParamsByTrade(guavaRegion.getValidRegionCode(),
                                                                            null,
                                                                            null,
                                                                            date),
                                                      this.getPage(page, size))
                                             .getContent()
                                             .stream()
                                             .peek(this::fillArea)
                                             .map(tradeSummary -> GuavaTradeResponse.transform(tradeSummary,
                                                                                               this.getMaxPrice(tradeSummary.getBuildingCode(),
                                                                                                                tradeSummary.getAreaCode())))
//                                   // fixme building id값 조회 안하도록
                                             .peek(x -> {
                                                 x.setDongName(guavaRegionRepository.findByRegionCode(x.getRegionId())
                                                                                    .map(GuavaRegion::getDisplayName)
                                                                                    .orElse(""));
                                                 x.setBuildingId(String.valueOf(buildingMappingRepository.findByBuildingCode(x.getBuildingId())
                                                                                                         .stream()
                                                                                                         .findFirst()
                                                                                                         .map(BuildingMapping::getId)
                                                                                                         .orElse(0l)));
                                             })
                                             .collect(Collectors.toList());
            } else {
                return rentSummaryRepository.findAll(this.getParamsByRent(guavaRegion.getValidRegionCode(),
                                                                          null,
                                                                          null,
                                                                          date),
                                                     this.getPage(page, size)).getContent()
                                            .stream()
                                            .peek(this::fillArea)
                                            .map(GuavaTradeResponse::transform)
//                                   // fixme building id값 조회 안하도록
                                            .peek(x -> {
                                                x.setDongName(guavaRegionRepository.findByRegionCode(x.getRegionId())
                                                                                   .map(GuavaRegion::getDisplayName)
                                                                                   .orElse(""));
                                                x.setBuildingId(String.valueOf(buildingMappingRepository.findByBuildingCode(x.getBuildingId())
                                                                                                        .stream()
                                                                                                        .findFirst()
                                                                                                        .map(BuildingMapping::getId)
                                                                                                        .orElse(0l)));
                                            })
                                            .collect(Collectors.toList());
            }
        }
        return Lists.newArrayList();
    }

    private Integer getMaxPrice(String buildingCode) {
        Optional<TradeSummary> optionalTradeSummary = tradeSummaryRepository.findTop1ByBuildingCodeOrderByPriceDesc(
            buildingCode);
        if (optionalTradeSummary.isPresent()) {
            return optionalTradeSummary.get().getPrice();
        }
        return 0;
    }

    @Cacheable("getMaxPrice")
    public Integer getMaxPrice(String buildingCode, String areaId) {
        if (StringUtils.isEmpty(areaId)) {
            return getMaxPrice(buildingCode);
        }
        Optional<GuavaBuildingArea> optionalGuavaBuildingArea = guavaBuildingAreaRepository.findById(Long.valueOf(areaId));
        if (optionalGuavaBuildingArea.isPresent()) {
            Optional<TradeSummary> optionalTradeSummary =
                tradeSummaryRepository.findTop1ByBuildingCodeAndAreaCodeOrderByPriceDesc(
                    buildingCode,
                    areaId);
            if (optionalTradeSummary.isPresent()) {
                return optionalTradeSummary.get().getPrice();
            }
        }
        return 0;
    }

    @Override
    public List<GuavaTradeResponse> getBuildingTradeList(String tradeType, String buildingId, Integer page, Integer size, String areaId, String date) {
        Optional<BuildingMapping> optionalBuildingMapping = buildingMappingRepository.findById(Long.valueOf(buildingId));
        if (optionalBuildingMapping.isPresent()) {
            BuildingMapping buildingMapping = optionalBuildingMapping.get();

            if ("trade".equals(tradeType)) {
                return tradeSummaryRepository.findAll(this.getParamsByTrade(null,
                                                                            buildingMapping.getBuildingCode(),
                                                                            areaId,
                                                                            date),
                                                      this.getPage(page, size)).getContent()
                                             .stream()
                                             .peek(this::fillArea)
                                             .map(tradeSummary -> GuavaTradeResponse.transform(tradeSummary,
                                                                                               this.getMaxPrice(tradeSummary.getBuildingCode(),
                                                                                                                tradeSummary.getAreaCode())))
//                                   // fixme building id값 조회 안하도록
                                             .peek(x -> x.setBuildingId(buildingId))
//                                             .peek(x -> x.setBeforeMaxPrice(String.valueOf(beforeMaxPrice)))
                                             .collect(Collectors.toList());
            } else {
                return rentSummaryRepository.findAll(this.getParamsByRent(null,
                                                                          buildingMapping.getBuildingCode(),
                                                                          areaId,
                                                                          date),
                                                     this.getPage(page, size)).getContent()
                                            .stream()
                                            .peek(this::fillArea)
                                            .map(GuavaTradeResponse::transform)
//                                   // fixme building id값 조회 안하도록
                                            .peek(x -> x.setBuildingId(buildingId))
                                            .collect(Collectors.toList());

            }
        }
        return Lists.newArrayList();
    }

    private PageRequest getPage(Integer page, Integer size) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
    }

    private Specification<TradeSummary> getParamsByTrade(String regionCode, String buildingCode, String areaCode, String date) {
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

    private Specification<RentSummary> getParamsByRent(String regionCode, String buildingCode, String areaCode, String date) {
        Map<String, Object> paramsMap = objectMapper.convertValue(GuavaTradeSearch.builder()
                                                                                  .regionCode(regionCode)
                                                                                  .buildingCode(buildingCode)
                                                                                  .areaCode(areaCode)
                                                                                  .date(date)
                                                                                  .build(), Map.class);
        Map<RentSummarySpecs.SearchKey, Object> params = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            if (ObjectUtils.isNotEmpty(entry.getValue()) && StringUtils.isNotEmpty(String.valueOf(entry.getValue()))) {
                params.put(RentSummarySpecs.SearchKey.convert(entry.getKey()), entry.getValue());
            }
        }
        return RentSummarySpecs.searchWith(params);
    }

    private void fillArea(RentSummary x) {
        if (x.getPublicArea() == null) {
            Optional<OpenApiRentInfo> openApiTradeInfo =
                openApiRentInfoRepository.findById(x.getId());
            if (openApiTradeInfo.isPresent()) {
                OpenApiRentInfo rentInfo = openApiTradeInfo.get();
                x.setPrivateArea(Double.valueOf(rentInfo.getArea()));
                x.setPublicArea(Double.valueOf(rentInfo.getArea()));
                x.setAreaType((int) (Double.valueOf(rentInfo.getArea()) * 0.3025) + "평");
            }
        }
    }

    private void fillArea(TradeSummary x) {
        if (x.getPublicArea() == null) {
            Optional<OpenApiTradeInfo> openApiTradeInfo =
                openApiTradeInfoRepository.findById(x.getId());
            if (openApiTradeInfo.isPresent()) {
                OpenApiTradeInfo tradeInfo = openApiTradeInfo.get();
                x.setPrivateArea(tradeInfo.getArea());
                x.setPublicArea(tradeInfo.getArea());
                x.setAreaType((int) (tradeInfo.getArea() * 0.3025) + "평");
            }
        }
    }
}
