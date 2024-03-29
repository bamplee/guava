package im.prize.api.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import im.prize.api.domain.oboo.TradeArticle;
import im.prize.api.hgnn.repository.BuildingMapping;
import im.prize.api.hgnn.repository.BuildingMappingRepository;
import im.prize.api.hgnn.repository.GuavaBuildingAreaRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingArea;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaMappingInfo;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaMappingInfoRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.TradeArticleRepository;
import im.prize.api.interfaces.GuavaTradeSearch;
import im.prize.api.interfaces.response.AreaResponse;
import im.prize.api.interfaces.response.GuavaTradeResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GuavaMarketServiceImpl implements GuavaMarketService {
//    private static final Integer PAGE_SIZE = 100;
    @Value("${app.kakao.apiKey}")
    private String kakaoMapApiKey;
    private final GuavaRegionRepository guavaRegionRepository;
    private final GuavaBuildingRepository guavaBuildingRepository;
    private final GuavaBuildingAreaRepository guavaBuildingAreaRepository;
    private final TradeArticleRepository tradeArticleRepository;
    private final BuildingMappingRepository buildingMappingRepository;
    private final RentSummaryRepository rentSummaryRepository;
    private final TradeSummaryRepository tradeSummaryRepository;
    private final ObjectMapper objectMapper;

    public static final DateTimeFormatter DATE_TIME_FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public GuavaMarketServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                  GuavaBuildingRepository guavaBuildingRepository,
                                  GuavaBuildingAreaRepository guavaBuildingAreaRepository,
                                  TradeArticleRepository tradeArticleRepository,
                                  BuildingMappingRepository buildingMappingRepository,
                                  RentSummaryRepository rentSummaryRepository,
                                  TradeSummaryRepository tradeSummaryRepository,
                                  ObjectMapper objectMapper) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
        this.tradeArticleRepository = tradeArticleRepository;
        this.buildingMappingRepository = buildingMappingRepository;
        this.rentSummaryRepository = rentSummaryRepository;
        this.tradeSummaryRepository = tradeSummaryRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<GuavaTradeResponse> getRegionMarketList(String tradeType,
                                                        String regionId,
                                                        Integer page,
                                                        Integer size,
                                                        Integer startArea,
                                                        Integer endArea) {
        Optional<GuavaRegion> optionalGuavaRegion = guavaRegionRepository.findById(Long.valueOf(regionId));
//        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findById(Long.valueOf(buildingId));
        if (!optionalGuavaRegion.isPresent()) {
            return Lists.newArrayList();
        }
        GuavaRegion guavaRegion = optionalGuavaRegion.get();
        List<TradeArticle> tradeArticleList = Lists.newArrayList();
        if (guavaRegion.getRegionType() == RegionType.DONG) {
            tradeArticleList = tradeArticleRepository.findByRegionCodeAndArea2Between(String.valueOf(guavaRegion.getRegionCode()),
                                                                                      startArea,
                                                                                      endArea,
                                                                                      PageRequest.of(page,
                                                                                                     size))
                                                     .stream()
                                                     .filter(x -> LocalDate.parse(x.getArticleConfirmYmd(),
                                                                                  DATE_TIME_FORMATTER_YYYYMMDD)
                                                                           .isAfter(LocalDate.now().minusMonths(1L)))
                                                     .collect(
                                                         Collectors.toList());

        } else if (guavaRegion.getRegionType() == RegionType.SIGUNGU) {
            tradeArticleList = tradeArticleRepository.findByRegionCodeLikeAndArea2Between(String.valueOf(guavaRegion.getSigunguCode() +
                                                                                                             "%"),
                                                                                          startArea,
                                                                                          endArea,
                                                                                          PageRequest.of(page,
                                                                                                         size))
                                                     .stream()
                                                     .filter(x -> LocalDate.parse(x.getArticleConfirmYmd(),
                                                                                  DATE_TIME_FORMATTER_YYYYMMDD)
                                                                           .isAfter(LocalDate.now().minusMonths(1L)))
                                                     .collect(
                                                         Collectors.toList());
        } else {
            tradeArticleList = tradeArticleRepository.findByRegionCodeLikeAndArea2Between(String.valueOf(guavaRegion.getSido() + "%"),
                                                                                          startArea,
                                                                                          endArea,
                                                                                          PageRequest.of(page,
                                                                                                         size))
                                                     .stream()
                                                     .filter(x -> LocalDate.parse(x.getArticleConfirmYmd(),
                                                                                  DATE_TIME_FORMATTER_YYYYMMDD)
                                                                           .isAfter(LocalDate.now().minusMonths(1L)))
                                                     .collect(
                                                         Collectors.toList());
        }

//        List<GuavaBuildingArea> guavaBuildingAreaList = guavaRegion.getAreaList();
        List<TradeArticle> progressList = tradeArticleList.stream()
                                                          .filter(x -> StringUtils.isEmpty(x.getEndDate()))
                                                          .filter(GuavaUtils.distinctByKeys(x -> getDupleCheckKey(x)))
                                                          .collect(Collectors.toList());
        List<String> dupleKeyList = progressList.stream().map(this::getDupleCheckKey).collect(Collectors.toList());
        List<TradeArticle> endList = tradeArticleList.stream()
                                                     .filter(x -> StringUtils.isNotEmpty(x.getEndDate()))
                                                     .filter(GuavaUtils.distinctByKeys(this::getDupleCheckKey))
                                                     .filter(x -> !dupleKeyList.contains(getDupleCheckKey(x)))
                                                     .collect(Collectors.toList());

        return Stream.concat(progressList.stream(), endList.stream())/*.peek(openApiTradeInfo -> {
            Optional<GuavaBuilding> first = guavaBuildingRepository.findByBuildingCode(openApiTradeInfo.getBuildingCode());
            if (first.isPresent()) {
                List<GuavaBuildingArea> guavaBuildingAreaList = first.get().getAreaList();
                GuavaBuildingArea areaBuildingArea = getAreaByPublicArea(guavaBuildingAreaList, openApiTradeInfo.getArea1());
                openApiTradeInfo.setAreaName(String.valueOf(areaBuildingArea.getId()));
                openApiTradeInfo.setArea1(String.valueOf(areaBuildingArea.getPrivateArea()));
                openApiTradeInfo.setArea2(String.valueOf(areaBuildingArea.getPublicArea()));
            }
        })*/.sorted(Comparator.comparing(TradeArticle::getArticleConfirmYmd).reversed())
                     .map((TradeArticle tradeType1) -> transform(tradeType, tradeType1))
                     .filter(Optional::isPresent)
                     .map(Optional::get)
                     .collect(Collectors.toList());
    }

    @Override
    public List<GuavaTradeResponse> getBuildingMarketList(String tradeType, String buildingId, Integer page, Integer size, String areaId) {
        Optional<BuildingMapping> optionalBuildingMapping = buildingMappingRepository.findById(Long.valueOf(buildingId));
        if (!optionalBuildingMapping.isPresent()) {
            return Lists.newArrayList();
        }
        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findByBuildingCode(optionalBuildingMapping.get()
                                                                                                                          .getBuildingCode());
        if (!optionalGuavaBuilding.isPresent()) {
            return Lists.newArrayList();
        }
        GuavaBuilding guavaBuilding = optionalGuavaBuilding.get();
        List<String> tradeTypeCode = Lists.newArrayList();
        if ("trade".equals(tradeType)) {
            tradeTypeCode.add("A1");
        } else {
            tradeTypeCode.add("B1");
            tradeTypeCode.add("B2");
        }
        List<TradeArticle> tradeArticleList =
            tradeArticleRepository.findByPortalIdAndTradeTypeCodeIn(String.valueOf(guavaBuilding.getPortalId()),
                                                                    tradeTypeCode,
                                                                    PageRequest.of(page,
                                                                                   size,
                                                                                   Sort.by(Sort.Direction.DESC,
                                                                                           "articleConfirmYmd")))
                                  .stream()
                                  .filter(x -> LocalDate.parse(x.getArticleConfirmYmd(),
                                                               DATE_TIME_FORMATTER_YYYYMMDD)
                                                        .isAfter(LocalDate.now().minusMonths(1L)))
                                  .collect(
                                      Collectors.toList());

        List<GuavaBuildingArea> guavaBuildingAreaList = guavaBuilding.getAreaList();
        List<TradeArticle> progressList = tradeArticleList.stream()
                                                          .filter(x -> StringUtils.isEmpty(x.getEndDate()))
                                                          .filter(GuavaUtils.distinctByKeys(x -> getDupleCheckKey(x)))
                                                          .collect(Collectors.toList());
        List<String> dupleKeyList = progressList.stream().map(this::getDupleCheckKey).collect(Collectors.toList());
        List<TradeArticle> endList = tradeArticleList.stream()
                                                     .filter(x -> StringUtils.isNotEmpty(x.getEndDate()))
                                                     .filter(GuavaUtils.distinctByKeys(this::getDupleCheckKey))
                                                     .filter(x -> !dupleKeyList.contains(getDupleCheckKey(x)))
                                                     .collect(Collectors.toList());

        return Stream.concat(progressList.stream(), endList.stream())/*.peek(tradeArticle -> {
            Optional<GuavaBuildingArea> first = guavaBuildingAreaList.stream()
                                                                     .filter(x -> x.getAreaType().equals(tradeArticle.getAreaName()))
                                                                     .findFirst();
            GuavaBuildingArea areaBuildingArea = first.orElse(getAreaByPublicArea(guavaBuildingAreaList, tradeArticle.getArea1()));
            tradeArticle.setAreaName(String.valueOf(areaBuildingArea.getId()));
            tradeArticle.setArea1(String.valueOf(areaBuildingArea.getPrivateArea()));
            tradeArticle.setArea2(String.valueOf(areaBuildingArea.getPublicArea()));
        }).filter(x -> !StringUtils.isNotEmpty(areaId) || x.getAreaName().equals(areaId))*/
                     .sorted(Comparator.comparing(TradeArticle::getArticleConfirmYmd).reversed())
                     .map((TradeArticle tradeType1) -> transform(tradeType, tradeType1))
                     .filter(Optional::isPresent)
                     .map(Optional::get)
                     .filter(x -> !StringUtils.isNotEmpty(areaId) || areaId.equals(x.getArea().getAreaId()))
                     .collect(Collectors.toList());
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

    private String getDupleCheckKey(TradeArticle tradeArticle) {
        return String.format("%s%s%s%s%s", tradeArticle.getFloorInfo().split("/").length > 0 ? tradeArticle.getFloorInfo()
                                                                                                           .split("/")[0] :
            tradeArticle.getFloorInfo(), tradeArticle.getDealOrWarrantPrc(), tradeArticle
                                 .getArea1(), tradeArticle.getArea2(), tradeArticle.getBuildingName());
    }

    private GuavaBuildingArea getAreaByPublicArea(List<GuavaBuildingArea> guavaBuildingAreaList, String publicArea) {
        Optional<GuavaBuildingArea> first = Optional.empty();
        first = guavaBuildingAreaList.stream()
                                     .filter(x -> x.getPrivateArea() == Double.parseDouble(String.format(
                                         "%.2f",
                                         Double.parseDouble(publicArea))))
                                     .findFirst();
        if (!first.isPresent()) {
            double min = Double.MAX_VALUE;
            for (GuavaBuildingArea guavaBuildingArea : guavaBuildingAreaList) {
                double a = Math.abs(guavaBuildingArea.getPublicArea() - Double.valueOf(publicArea));  // 절대값을 취한다.
                if (min > a) {
                    min = a;
                    first = Optional.of(guavaBuildingArea);
                }
            }
        }

        return first.orElse(GuavaBuildingArea.builder().privateArea(0d).publicArea(0d).build());
    }

    private Optional<GuavaTradeResponse> transform(String tradeType, TradeArticle tradeArticle) {
        Optional<BuildingMapping> optionalGuavaMappingInfo = buildingMappingRepository.findByBuildingCode(tradeArticle.getBuildingCode())
                                                                                      .stream()
                                                                                      .findFirst();
//        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findByBuildingCode(tradeArticle.getBuildingCode());

        if(!optionalGuavaMappingInfo.isPresent()) {
            return Optional.empty();
        }
        List<GuavaBuildingArea> areaList = guavaBuildingAreaRepository.findByBuildingCode(tradeArticle.getBuildingCode());
        Optional<GuavaBuildingArea> first = areaList.stream()
                                                    .filter(x -> x.getAreaType()
                                                                  .toLowerCase()
                                                                  .equals(tradeArticle.getAreaName().toLowerCase()))
                                                    .findFirst();
        if (!first.isPresent()) {
            first = areaList.stream()
                            .filter(x -> x.getAreaType().replace("타입", "").toLowerCase().contains(tradeArticle.getAreaName().toLowerCase()))
                            .findFirst();
        }
        if (!first.isPresent()) {
            first = areaList.stream()
                            .filter(x -> x.getAreaType().toLowerCase().contains(tradeArticle.getAreaName().toLowerCase()))
                            .findFirst();
        }
        if (!first.isPresent()) {
            first = Optional.of(GuavaUtils.getAreaByPrivateArea(areaList,
                                                                String.valueOf(tradeArticle.getArea2())));
        }
        GuavaBuildingArea areaByPrivateArea = first.get();
//        GuavaBuildingArea areaByPrivateArea = GuavaUtils.getAreaByPrivateArea(optionalGuavaBuilding.get().getAreaList(),
//                                                                              String.valueOf(tradeArticle.getArea1()));
        LocalDate yyyyMMdd = LocalDate.parse(tradeArticle.getStartDate(), DATE_TIME_FORMATTER_YYYYMMDD);
        int untilDays = LocalDate.parse(tradeArticle.getArticleConfirmYmd(), DATE_TIME_FORMATTER_YYYYMMDD)
                                 .until(LocalDate.now())
                                 .getDays();
//        String beforeHighPrice = openApiTradeInfoRepository.getMaxPrice(yyyyMMdd.format(DATE_TIME_FORMATTER_YYYYMMDD),
//                                                                        openApiTradeInfo.getBuildingId(),
//                                                                        openApiTradeInfo.getArea());

        String beforeMaxPrice = "";
        if ("trade".equals(tradeType)) {
            Optional<TradeSummary> tradeSummary =
                tradeSummaryRepository.findTop1ByBuildingCodeAndAreaTypeOrderByPriceDesc(
                    tradeArticle.getBuildingCode(),
                    areaByPrivateArea.getAreaType());
            if (tradeSummary.isPresent()) {
                beforeMaxPrice = String.valueOf(tradeSummary.get().getPrice());
            }
        } else {
            Optional<RentSummary> rentSummary =
                rentSummaryRepository.findTop1ByBuildingCodeAndAreaTypeOrderByPriceDesc(
                    tradeArticle.getBuildingCode(),
                    areaByPrivateArea.getAreaType());
            if (rentSummary.isPresent()) {
                beforeMaxPrice = String.valueOf(rentSummary.get().getPrice());
            }
        }

        String articleFeatureDesc = tradeArticle.getArticleFeatureDesc();
        boolean isRent = false;
        if (StringUtils.isNotEmpty(articleFeatureDesc)) {
            // fixme
            String str = articleFeatureDesc.replaceAll(" ", "");
            isRent = str.contains("세안고");
        }
        return Optional.of(GuavaTradeResponse.builder()
                                 .isRent(isRent)
                                 .name(optionalGuavaMappingInfo.get().getBuildingName())
                                 .dongName(tradeArticle.getBuildingName())
                                 .buildingId(String.valueOf(optionalGuavaMappingInfo.get().getId()))
                                 .regionId(String.valueOf(guavaRegionRepository.findByRegionCode(tradeArticle.getRegionCode())
                                                                               .map(GuavaRegion::getId)
                                                                               .orElse(0L)))
                                 .type("MARKET")
                                 .date(untilDays == 0 ? "오늘" : untilDays + "일전")
                                 .year(yyyyMMdd.format(DateTimeFormatter.ofPattern("yyyy")))
                                 .month(yyyyMMdd.format(DateTimeFormatter.ofPattern("MM")))
                                 .day(yyyyMMdd.format(DateTimeFormatter.ofPattern("dd")))
                                 .floor(tradeArticle.getFloorInfo().split("/").length > 0 ? tradeArticle.getFloorInfo()
                                                                                                        .split("/")[0] :
                                            tradeArticle.getFloorInfo())
                                 .price(String.valueOf(tradeArticle.getPrice()))
                                 .priceName(tradeArticle.getSummaryPrice())
                                 .subPrice(tradeArticle.getRentPrc())
                                 .beforeMaxPrice(beforeMaxPrice)
                                 .beforeMaxPriceName(GuavaUtils.getTradePrice(beforeMaxPrice))
                                 .minusPrice(StringUtils.isNotEmpty(beforeMaxPrice) ?
                                                 String.valueOf(tradeArticle.getPrice() - Integer.valueOf(
                                                     beforeMaxPrice)) : "-")
                                 .minusPriceName(StringUtils.isNotEmpty(beforeMaxPrice) ?
                                                     String.valueOf(tradeArticle.getPrice() - Integer.valueOf(
                                                         beforeMaxPrice)) : "-")
                                 .area(AreaResponse.builder()
                                                   .areaId(String.valueOf(areaByPrivateArea.getId()))
                                                   .type(areaByPrivateArea.getAreaType().replace("타입", "") + "㎡")
                                                   .name((int) (areaByPrivateArea.getPublicArea() * 0.3025) + "평")
                                                   .privateArea(String.valueOf(
                                                       areaByPrivateArea.getPrivateArea()))
                                                   .publicArea(String.valueOf(areaByPrivateArea.getPublicArea()))
                                                   .build())
                                 .isActive(tradeArticle.getEndDate() == null)
                                 .isNew(LocalDate.parse(tradeArticle.getArticleConfirmYmd(),
                                                        DATE_TIME_FORMATTER_YYYYMMDD).equals(LocalDate.now()))
                                 .isHighPrice(StringUtils.isNotEmpty(beforeMaxPrice) && Integer.valueOf(beforeMaxPrice) < tradeArticle.getPrice())
                                 .build());
    }
}
