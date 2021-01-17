package im.prize.api.application;

import im.prize.api.domain.oboo.OpenApiTradeInfo;
import im.prize.api.domain.oboo.TradeArticle;
import im.prize.api.hgnn.repository.BuildingMapping;
import im.prize.api.hgnn.repository.BuildingMappingRepository;
import im.prize.api.hgnn.repository.GuavaBuildingAreaRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingArea;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionStats;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionStatsRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.OpenApiTradeInfoRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.TradeArticleRepository;
import im.prize.api.interfaces.response.AreaResponse;
import im.prize.api.interfaces.response.GuavaBuildingDetailResponse;
import im.prize.api.interfaces.response.GuavaSummaryResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GuavaSummaryServiceImpl implements GuavaSummaryService {
    @Value("${app.kakao.apiKey}")
    private String kakaoMapApiKey;
    private final GuavaRegionRepository guavaRegionRepository;
    private final GuavaBuildingRepository guavaBuildingRepository;
    private final EntityManager entityManager;
    private final OpenApiTradeInfoRepository openApiTradeInfoRepository;
    private final TradeArticleRepository tradeArticleRepository;
    private final GuavaRegionStatsRepository guavaRegionStatsRepository;
    private final GuavaBuildingAreaRepository guavaBuildingAreaRepository;
    private final BuildingMappingRepository buildingMappingRepository;

    public static final DateTimeFormatter DATE_TIME_FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public GuavaSummaryServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                   GuavaBuildingRepository guavaBuildingRepository,
                                   EntityManager entityManager,
                                   OpenApiTradeInfoRepository openApiTradeInfoRepository,
                                   TradeArticleRepository tradeArticleRepository,
                                   GuavaRegionStatsRepository guavaRegionStatsRepository,
                                   GuavaBuildingAreaRepository guavaBuildingAreaRepository,
                                   BuildingMappingRepository buildingMappingRepository) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.entityManager = entityManager;
        this.openApiTradeInfoRepository = openApiTradeInfoRepository;
        this.tradeArticleRepository = tradeArticleRepository;
        this.guavaRegionStatsRepository = guavaRegionStatsRepository;
        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
        this.buildingMappingRepository = buildingMappingRepository;
    }

    @Override
    public List<GuavaSummaryResponse> getSummary(Integer level,
                                                 Double northEastLng,
                                                 Double northEastLat,
                                                 Double southWestLng,
                                                 Double southWestLat,
                                                 Integer startArea,
                                                 Integer endArea) {
        RegionType type = GuavaUtils.convertLevelToRegionType(level);
        if (type == RegionType.BUILDING) {
            return this.getBuildingList(northEastLng, northEastLat, southWestLng, southWestLat)
                       .parallelStream()
                       .map(this::transform)
                       .filter(Optional::isPresent)
                       .map(Optional::get)
                       .collect(Collectors.toList());
        }
        return this.getRegionList(northEastLng, northEastLat, southWestLng, southWestLat)
                   .parallelStream()
                   .filter(x -> x.getRegionType() == type)
                   .map(this::transform)
                   .filter(ObjectUtils::isNotEmpty)
                   .peek(x -> x.setType(type))
                   .collect(Collectors.toList());
    }

    @Override
    public GuavaBuildingDetailResponse getBuildingDetail(String buildingId) {
        Optional<BuildingMapping> optionalBuildingMapping = buildingMappingRepository.findById(Long.valueOf(buildingId));
        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findByBuildingCode(optionalBuildingMapping.get().getBuildingCode());
        if (optionalGuavaBuilding.isPresent()) {
            GuavaBuilding guavaBuilding = optionalGuavaBuilding.get();

            int parkingInside = guavaBuilding.getParkingInside() != null ? guavaBuilding.getParkingInside() : 0;
            int parkingOutSide = guavaBuilding.getParkingOutside() != null ? guavaBuilding.getParkingOutside() : 0;
            int parkingTotal = guavaBuilding.getParkingTotal() != null && guavaBuilding.getParkingTotal() > 0 ?
                guavaBuilding.getParkingTotal() : guavaBuilding
                .getParkingInside() + guavaBuilding.getParkingOutside();
            return GuavaBuildingDetailResponse.builder()
                                              .id(guavaBuilding.getId())
                                              .regionId(String.valueOf(guavaRegionRepository.findByRegionCode(guavaBuilding.getRegionCode())
                                                                                            .get()
                                                                                            .getId()))
                                              .name(guavaBuilding.getName())
                                              .address(guavaBuilding.getAddress())
                                              .dongCount(Optional.ofNullable(guavaBuilding.getBuildingCount()).orElse(0))
                                              .hoCount(Optional.ofNullable(guavaBuilding.getTotalHousehold()).orElse(0))
                                              .floorAreaRatio(guavaBuilding.getFloorAreaRatio().intValue())
                                              .aptType(guavaBuilding.getAsileType())
                                              .buildingCoverageRatio(guavaBuilding.getBuildingCoverageRatio().intValue())
                                              .buildingCount(guavaBuilding.getBuildingCount())
                                              .maxFloor(guavaBuilding.getFloorMax())
                                              .minFloor(guavaBuilding.getFloorMin())
                                              .since(Optional.ofNullable(guavaBuilding.getStartMonth())
                                                             .map(x -> x.split("T")[0])
                                                             .map(LocalDate::parse)
                                                             .map(x -> x.format(DateTimeFormatter.ofPattern("yyyy년 M월")))
                                                             .orElse(""))
                                              .parkingInside(parkingInside)
                                              .parkingOutside(parkingOutSide)
                                              .parkingTotal(parkingTotal)
                                              .lat(guavaBuilding.getPoint().getY())
                                              .lng(guavaBuilding.getPoint().getX())
                                              .areaList(guavaBuilding.getAreaList().stream()
                                                                     .map(AreaResponse::transform)
                                                                     .filter(Optional::isPresent)
                                                                     .map(Optional::get)
                                                                     .collect(Collectors.toList()))
                                              .build();
        }
        return null;
    }

//    @Override
//    public GuavaSearchResponse getRegion(Double lat, Double lng) {
//        List<GuavaRegion> guavaRegionList = this.searchGuavaRegion(lng, lat);
//        if (guavaRegionList.size() > 0) {
//            GuavaRegion guavaRegion = guavaRegionList.get(0);
//            return this.transformByPlace(guavaRegion);
//        }
//
//        return GuavaSearchResponse.builder()
//                                 .type(RegionType.DONG)
//                                 .id("22140")
//                                 .name("경기도 성남시 분당구 정자동")
//                                 .lat(37.3614463)
//                                 .lng(127.1114893)
//                                 .build();
//    }

//    @Override
//    public List<GuavaSearchResponse> getBuildings(String query) {
//        Pageable limit = PageRequest.of(0, 100);
//        Page<GuavaBuilding> guavaRegionPage = guavaBuildingRepository.findAll(GuavaBuildingRepository.getRegions(query), limit);
//        return guavaRegionPage.get()
//                              .filter(x -> x.getType() == 0)
//                              .map(x -> this.transformByPlace(guavaRegionRepository.findByRegionCode(x.getRegionCode()).get(), x))
//                              .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<GuavaChartResponse> getChartList(String buildingCode, String areaId, Long sinceYear) {
//        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findById(Long.valueOf(buildingCode));
//        List<OpenApiTradeInfo> infos = Lists.newArrayList();
//        String baseYear = String.valueOf(YearMonth.now().minusMonths(sinceYear).getYear());
//        if (optionalGuavaBuilding.isPresent()) {
//            GuavaBuilding guavaBuilding = optionalGuavaBuilding.get();
//            Optional<GuavaBuildingArea> optionalGuavaBuildingArea = Optional.empty();
//            if (StringUtils.isNotEmpty(areaId)) {
//                optionalGuavaBuildingArea = this.getArea(areaId);
//                if (optionalGuavaBuildingArea.isPresent()) {
//                    infos =
//                        openApiTradeInfoRepository.findByBuildingIdAndAreaAndYearGreaterThanEqualOrderByDateDesc(guavaBuilding
// .getBuildingCode(),
//                                                                                                                 optionalGuavaBuildingArea
//                                                                                                                     .get()
//                                                                                                                     .getPrivateArea(),
//                                                                                                                 baseYear);
//                }
//            }
//
//            if (infos.size() == 0) {
//                infos = openApiTradeInfoRepository.findByBuildingIdAndYearGreaterThanEqualOrderByDateDesc(guavaBuilding.getBuildingCode(),
//                                                                                                          baseYear);
//            }
//        }
//        return infos.stream().map(this::transformChartData).collect(Collectors.toList());
//    }

/*
    private GuavaChartResponse transformChartData(OpenApiTradeInfo openApiTradeInfo) {
        return GuavaChartResponse.builder()
                                 .date(openApiTradeInfo.getDate())
                                 .area(String.valueOf(openApiTradeInfo.getArea()))
//                                 .floor(openApiTradeInfo.getFloor())
                                 .price(openApiTradeInfo.getPrice().replace(",", ""))
                                 .build();
    }
*/

//    public Optional<GuavaBuildingArea> getArea(String areaId) {
//        return guavaBuildingAreaRepository.findById(Long.valueOf(areaId));
//    }

//    @Override
//    public List<GuavaTradeResponse> getBuildingMarketList(String buildingId, Integer page, String areaId) {
//        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findById(Long.valueOf(buildingId));
//        if (!optionalGuavaBuilding.isPresent()) {
//            return Lists.newArrayList();
//        }
//        GuavaBuilding guavaBuilding = optionalGuavaBuilding.get();
//        List<TradeArticle> tradeArticleList = tradeArticleRepository.findByPortalId(String.valueOf(guavaBuilding.getPortalId()),
//                                                                                    PageRequest.of(0,
//                                                                                                   100,
//                                                                                                   Sort.by(Sort.Direction.DESC,
//                                                                                                           "articleConfirmYmd")))
//                                                                    .stream()
//                                                                    .filter(x -> LocalDate.parse(x.getArticleConfirmYmd(),
//                                                                                                 DATE_TIME_FORMATTER_YYYYMMDD)
//                                                                                          .isAfter(LocalDate.now().minusMonths(1L)))
//                                                                    .collect(
//                                                                        Collectors.toList());
//
//        List<GuavaBuildingArea> guavaBuildingAreaList = guavaBuilding.getAreaList();
//        List<TradeArticle> progressList = tradeArticleList.stream()
//                                                          .filter(x -> StringUtils.isEmpty(x.getEndDate()))
//                                                          .filter(GuavaUtils.distinctByKeys(x -> getDupleCheckKey(x)))
//                                                          .collect(Collectors.toList());
//        List<String> dupleKeyList = progressList.stream().map(this::getDupleCheckKey).collect(Collectors.toList());
//        List<TradeArticle> endList = tradeArticleList.stream()
//                                                     .filter(x -> StringUtils.isNotEmpty(x.getEndDate()))
//                                                     .filter(GuavaUtils.distinctByKeys(this::getDupleCheckKey))
//                                                     .filter(x -> !dupleKeyList.contains(getDupleCheckKey(x)))
//                                                     .collect(Collectors.toList());
//
//        return Stream.concat(progressList.stream(), endList.stream()).peek(tradeArticle -> {
//            Optional<GuavaBuildingArea> first = guavaBuildingAreaList.stream()
//                                                                     .filter(x -> x.getAreaType().equals(tradeArticle.getAreaName()))
//                                                                     .findFirst();
//            GuavaBuildingArea areaBuildingArea = first.orElse(getAreaByPublicArea(guavaBuildingAreaList, tradeArticle.getArea1()));
//            tradeArticle.setAreaName(String.valueOf(areaBuildingArea.getId()));
//            tradeArticle.setArea1(String.valueOf(areaBuildingArea.getPrivateArea()));
//            tradeArticle.setArea2(String.valueOf(areaBuildingArea.getPublicArea()));
//        }).filter(x -> !StringUtils.isNotEmpty(areaId) || x.getAreaName().equals(areaId))
//                     .sorted(Comparator.comparing(TradeArticle::getArticleConfirmYmd).reversed())
//                     .map(this::transform).collect(Collectors.toList());
//    }

//    @Override
//    public List<GuavaChartResponse> getRegionChartList(String regionId, Integer startArea, Integer endArea, Long beforeMonth) {
//        Optional<GuavaRegion> byId = guavaRegionRepository.findById(Long.valueOf(regionId));
//        if (!byId.isPresent()) {
//            return Lists.newArrayList();
//        }
//        GuavaRegion guavaRegion = byId.get();
//        List<OpenApiTradeInfo> openApiTradeInfoList = Lists.newArrayList();
//        String baseYear = String.valueOf(YearMonth.now().minusMonths(beforeMonth).getYear());
//        if (guavaRegion.getRegionType() == RegionType.DONG) {
//            if (startArea + endArea == 0) {
//                openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndYearGreaterThanOrderByDateDesc(
//                    guavaRegion.getSigunguCode(),
//                    guavaRegion.getDongCode(),
//                    baseYear);
//            } else {
//                openApiTradeInfoList =
//                    openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAreaBetweenAndYearGreaterThanEqualOrderByDateDesc(
//                        guavaRegion.getSigunguCode(),
//                        guavaRegion.getDongCode(),
//                        Double.valueOf(startArea),
//                        Double.valueOf(endArea),
//                        baseYear);
//            }
//        } else if (guavaRegion.getRegionType() == RegionType.SIGUNGU) {
//            if (startArea + endArea == 0) {
//                openApiTradeInfoList = openApiTradeInfoRepository.findByDongSigunguCodeAndYearGreaterThanOrderByDateDesc(
//                    guavaRegion.getSigunguCode(),
//                    baseYear);
//            } else {
//                openApiTradeInfoList = openApiTradeInfoRepository
// .findByDongSigunguCodeAndAreaBetweenAndYearGreaterThanEqualOrderByDateDesc(
//                    guavaRegion.getSigunguCode(),
//                    Double.valueOf(startArea),
//                    Double.valueOf(endArea),
//                    baseYear);
//            }
//        }
//        return openApiTradeInfoList.stream()
//                                   .filter(x -> StringUtils.isNotEmpty(x.getBuildingId()))
//                                   .map(this::transformChartData)
//                                   .collect(Collectors.toList());
//    }

//    @Override
//    public List<GuavaTradeResponse> getRegionMarketList(String regionId, Integer page, Integer startArea, Integer endArea) {
//        Optional<GuavaRegion> optionalGuavaRegion = guavaRegionRepository.findById(Long.valueOf(regionId));
////        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findById(Long.valueOf(buildingId));
//        if (!optionalGuavaRegion.isPresent()) {
//            return Lists.newArrayList();
//        }
//        GuavaRegion guavaRegion = optionalGuavaRegion.get();
//        List<TradeArticle> tradeArticleList = Lists.newArrayList();
//        if (guavaRegion.getRegionType() == RegionType.DONG) {
//            tradeArticleList = tradeArticleRepository.findByRegionCodeAndArea2Between(String.valueOf(guavaRegion.getRegionCode()),
//                                                                                      startArea,
//                                                                                      endArea,
//                                                                                      PageRequest.of(0,
//                                                                                                     100))
//                                                     .stream()
//                                                     .filter(x -> LocalDate.parse(x.getArticleConfirmYmd(),
//                                                                                  DATE_TIME_FORMATTER_YYYYMMDD)
//                                                                           .isAfter(LocalDate.now().minusMonths(1L)))
//                                                     .collect(
//                                                         Collectors.toList());
//
//        } else if (guavaRegion.getRegionType() == RegionType.SIGUNGU) {
//            tradeArticleList = tradeArticleRepository.findByRegionCodeLikeAndArea2Between(String.valueOf(guavaRegion.getSigunguCode() +
//                                                                                                             "%"),
//                                                                                          startArea,
//                                                                                          endArea,
//                                                                                          PageRequest.of(0,
//                                                                                                         100))
//                                                     .stream()
//                                                     .filter(x -> LocalDate.parse(x.getArticleConfirmYmd(),
//                                                                                  DATE_TIME_FORMATTER_YYYYMMDD)
//                                                                           .isAfter(LocalDate.now().minusMonths(1L)))
//                                                     .collect(
//                                                         Collectors.toList());
//        } else {
//            tradeArticleList = tradeArticleRepository.findByRegionCodeLikeAndArea2Between(String.valueOf(guavaRegion.getSido() + "%"),
//                                                                                          startArea,
//                                                                                          endArea,
//                                                                                          PageRequest.of(0,
//                                                                                                         100))
//                                                     .stream()
//                                                     .filter(x -> LocalDate.parse(x.getArticleConfirmYmd(),
//                                                                                  DATE_TIME_FORMATTER_YYYYMMDD)
//                                                                           .isAfter(LocalDate.now().minusMonths(1L)))
//                                                     .collect(
//                                                         Collectors.toList());
//        }
//
////        List<GuavaBuildingArea> guavaBuildingAreaList = guavaRegion.getAreaList();
//        List<TradeArticle> progressList = tradeArticleList.stream()
//                                                          .filter(x -> StringUtils.isEmpty(x.getEndDate()))
//                                                          .filter(GuavaUtils.distinctByKeys(x -> getDupleCheckKey(x)))
//                                                          .collect(Collectors.toList());
//        List<String> dupleKeyList = progressList.stream().map(this::getDupleCheckKey).collect(Collectors.toList());
//        List<TradeArticle> endList = tradeArticleList.stream()
//                                                     .filter(x -> StringUtils.isNotEmpty(x.getEndDate()))
//                                                     .filter(GuavaUtils.distinctByKeys(this::getDupleCheckKey))
//                                                     .filter(x -> !dupleKeyList.contains(getDupleCheckKey(x)))
//                                                     .collect(Collectors.toList());
//
//        return Stream.concat(progressList.stream(), endList.stream()).peek(openApiTradeInfo -> {
//            Optional<GuavaBuilding> first = guavaBuildingRepository.findByBuildingCode(openApiTradeInfo.getBuildingCode())
//                                                                   .stream()
//                                                                   .filter(x -> x.equals(openApiTradeInfo.getBuildingCode()))
//                                                                   .findFirst();
//            if (first.isPresent()) {
//                List<GuavaBuildingArea> guavaBuildingAreaList = first.get().getAreaList();
//                GuavaBuildingArea areaBuildingArea = getAreaByPublicArea(guavaBuildingAreaList, openApiTradeInfo.getArea1());
//                openApiTradeInfo.setAreaName(String.valueOf(areaBuildingArea.getId()));
//                openApiTradeInfo.setArea1(String.valueOf(areaBuildingArea.getPrivateArea()));
//                openApiTradeInfo.setArea2(String.valueOf(areaBuildingArea.getPublicArea()));
//            }
//        }).sorted(Comparator.comparing(TradeArticle::getArticleConfirmYmd).reversed())
//                     .map(this::transform).collect(Collectors.toList());
//    }

//    @Override
//    public List<GuavaSearchResponse> getPlaceBuildings(String regionId) {
//        Optional<GuavaRegion> optionalGuavaRegion = guavaRegionRepository.findById(Long.valueOf(regionId));
//        if (optionalGuavaRegion.isPresent()) {
//            GuavaRegion guavaRegion = optionalGuavaRegion.get();
//            return guavaBuildingRepository.findByRegionCode(guavaRegion.getRegionCode()).stream()
//                                          .filter(x -> x.getType() == 0)
//                                          .map(x -> GuavaSearchResponse.transform(guavaRegion, x))
//                                          .collect(Collectors.toList());
//        }
//        return Lists.newArrayList();
//    }

/*
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
                        openApiTradeInfoRepository.findByBuildingIdAndAreaAndYearGreaterThanEqualOrderByDateDesc(guavaBuilding
                        .getBuildingCode(),
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
*/

//    private GuavaTradeResponse transform(TradeArticle tradeArticle) {
//        Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findByBuildingCode(tradeArticle.getBuildingCode())
//                                                                               .stream()
//                                                                               .filter(x -> x.getBuildingCode()
//                                                                                             .equals(tradeArticle.getBuildingCode()))
//                                                                               .findFirst();
//        GuavaBuildingArea areaByPrivateArea = GuavaUtils.getAreaByPrivateArea(optionalGuavaBuilding.get().getAreaList(),
//                                                                              String.valueOf(tradeArticle.getArea1()));
//        LocalDate yyyyMMdd = LocalDate.parse(tradeArticle.getStartDate(), DATE_TIME_FORMATTER_YYYYMMDD);
//        int untilDays = LocalDate.parse(tradeArticle.getArticleConfirmYmd(), DATE_TIME_FORMATTER_YYYYMMDD)
//                                 .until(LocalDate.now())
//                                 .getDays();
////        String beforeHighPrice = openApiTradeInfoRepository.getMaxPrice(yyyyMMdd.format(DATE_TIME_FORMATTER_YYYYMMDD),
////                                                                        openApiTradeInfo.getBuildingId(),
////                                                                        openApiTradeInfo.getArea());
//        String beforeMaxPrice = openApiTradeInfoRepository.getMaxPrice(LocalDate.now().format(DATE_TIME_FORMATTER_YYYYMMDD),
//                                                                       tradeArticle.getBuildingCode(),
//                                                                       areaByPrivateArea.getPrivateArea());
//        if (StringUtils.isEmpty(beforeMaxPrice)) {
//            beforeMaxPrice = openApiTradeInfoRepository.getMaxPriceByAreahundredthsDecimal(LocalDate.now()
//                                                                                                    .format(DATE_TIME_FORMATTER_YYYYMMDD),
//                                                                                           tradeArticle.getBuildingCode(),
//                                                                                           Math.round(areaByPrivateArea.getPrivateArea
// () * 100) / 100.0);
//        }
//        if (StringUtils.isEmpty(beforeMaxPrice)) {
//            beforeMaxPrice = openApiTradeInfoRepository.getMaxPriceByAreaTenthsDecimal(LocalDate.now().format
// (DATE_TIME_FORMATTER_YYYYMMDD),
//                                                                                       tradeArticle.getBuildingCode(),
//                                                                                       Math.round(areaByPrivateArea.getPrivateArea() *
// 100) / 100.0);
//        }
//
//        String articleFeatureDesc = tradeArticle.getArticleFeatureDesc();
//        boolean isRent = false;
//        if (StringUtils.isNotEmpty(articleFeatureDesc)) {
//            // fixme
//            String str = articleFeatureDesc.replaceAll(" ", "");
//            isRent = str.contains("세안고");
//        }
//        return GuavaTradeResponse.builder()
//                                 .isRent(isRent)
//                                 .name(optionalGuavaBuilding.get().getName())
//                                 .dongName(tradeArticle.getBuildingName())
//                                 .buildingId(String.valueOf(optionalGuavaBuilding
//                                                                .map(GuavaBuilding::getId)
//                                                                .orElse(0L)))
//                                 .regionId(String.valueOf(guavaRegionRepository.findByRegionCode(tradeArticle.getRegionCode())
//                                                                               .map(GuavaRegion::getId)
//                                                                               .orElse(0L)))
//                                 .type("MARKET")
//                                 .date(untilDays == 0 ? "오늘" : untilDays + "일전")
//                                 .year(yyyyMMdd.format(DateTimeFormatter.ofPattern("yyyy")))
//                                 .month(yyyyMMdd.format(DateTimeFormatter.ofPattern("MM")))
//                                 .day(yyyyMMdd.format(DateTimeFormatter.ofPattern("dd")))
//                                 .floor(tradeArticle.getFloorInfo().split("/").length > 0 ? tradeArticle.getFloorInfo()
//                                                                                                        .split("/")[0] :
//                                            tradeArticle.getFloorInfo())
//                                 .price(String.valueOf(tradeArticle.getPrice()))
//                                 .priceName(tradeArticle.getTradePrice())
//                                 .beforeMaxPrice(beforeMaxPrice)
//                                 .beforeMaxPriceName(GuavaUtils.getTradePrice(beforeMaxPrice))
//                                 .minusPrice(StringUtils.isNotEmpty(beforeMaxPrice) ?
//                                                 String.valueOf(tradeArticle.getPrice() - Integer.valueOf(
//                                                     beforeMaxPrice)) : "-")
//                                 .minusPriceName(StringUtils.isNotEmpty(beforeMaxPrice) ?
//                                                     String.valueOf(tradeArticle.getPrice() - Integer.valueOf(
//                                                         beforeMaxPrice)) : "-")
//                                 .area(AreaResponse.builder()
//                                                   .areaId(String.valueOf(areaByPrivateArea.getId()))
//                                                   .type(areaByPrivateArea.getAreaType().replace("타입", "") + "㎡")
//                                                   .name((int) (areaByPrivateArea.getPublicArea() * 0.3025) + "평")
//                                                   .privateArea(String.valueOf(
//                                                       areaByPrivateArea.getPrivateArea()))
//                                                   .publicArea(String.valueOf(areaByPrivateArea.getPublicArea()))
//                                                   .build())
//                                 .isActive(tradeArticle.getEndDate() == null)
//                                 .isNew(LocalDate.parse(tradeArticle.getArticleConfirmYmd(),
//                                                        DATE_TIME_FORMATTER_YYYYMMDD).equals(LocalDate.now()))
//                                 .isHighPrice(StringUtils.isNotEmpty(beforeMaxPrice) && Integer.valueOf(beforeMaxPrice) < tradeArticle
// .getPrice())
//                                 .build();
//    }

    private Optional<AreaResponse> transform(GuavaBuildingArea guavaBuildingArea) {
        if (guavaBuildingArea == null || guavaBuildingArea.getId() == null) {
            return Optional.empty();
        }
        return Optional.of(AreaResponse.builder()
                                       .areaId(String.valueOf(guavaBuildingArea.getId()))
                                       .type(guavaBuildingArea.getAreaType().replace("타입", "") + "㎡")
                                       .privateArea(String.valueOf(guavaBuildingArea.getPrivateArea()))
                                       .publicArea(String.valueOf(guavaBuildingArea.getPublicArea()))
                                       .name((int) (guavaBuildingArea.getPublicArea() * 0.3025) + "평")
                                       .hoCount(String.valueOf(guavaBuildingArea.getTotalHousehold()))
                                       .build());
    }

    private GuavaBuildingArea getAreaByPrivateArea(List<GuavaBuildingArea> guavaBuildingAreaList, String privateArea) {
        Optional<GuavaBuildingArea> first = Optional.empty();
        first = guavaBuildingAreaList.stream()
                                     .filter(x -> x.getPrivateArea() == Double.parseDouble(String.format(
                                         "%.2f",
                                         Double.parseDouble(privateArea))))
                                     .findFirst();
        if (!first.isPresent()) {
            double min = Double.MAX_VALUE;
            for (GuavaBuildingArea guavaBuildingArea : guavaBuildingAreaList) {
                double a = Math.abs(guavaBuildingArea.getPrivateArea() - Double.valueOf(privateArea));  // 절대값을 취한다.
                if (min > a) {
                    min = a;
                    first = Optional.of(guavaBuildingArea);
                }
            }
        }

        return first.orElse(GuavaBuildingArea.builder().privateArea(0d).publicArea(0d).build());
    }

    private List<GuavaRegion> getRegionList(Double northEastLng,
                                            Double northEastLat,
                                            Double southWestLng,
                                            Double southWestLat) {
        return this.searchGuavaRegion(northEastLng, northEastLat, southWestLng, southWestLat);
    }

    private List<BuildingMapping> getBuildingList(Double northEastLng,
                                                  Double northEastLat,
                                                  Double southWestLng,
                                                  Double southWestLat) {
        return this.searchGuavaBuilding(northEastLng, northEastLat, southWestLng, southWestLat);
    }

    public Optional<GuavaSummaryResponse> transform(BuildingMapping buildingMapping) {
        List<GuavaBuildingArea> byBuildingCode = guavaBuildingAreaRepository.findByBuildingCode(buildingMapping.getBuildingCode());
        return Optional.of(GuavaSummaryResponse.builder()
                                               .type(RegionType.BUILDING)
                                               .id(String.valueOf(buildingMapping.getId()))
                                               .lat(buildingMapping.getPoint().getY())
                                               .lng(buildingMapping.getPoint().getX())
//                                               .price(openApiTradeInfo.getSummaryPrice())
//                                               .marketPrice(marketPrice)
                                               .name(buildingMapping.getBuildingName())
                                               .build());
    }

    public Optional<GuavaSummaryResponse> transform(GuavaBuilding guavaBuilding, Integer startArea, Integer endArea) {
        Optional<OpenApiTradeInfo> optionalTrade =
            openApiTradeInfoRepository.findTop100ByBuildingIdAndAreaBetweenOrderByDateDesc(guavaBuilding.getBuildingCode(),
                                                                                           Double.valueOf(startArea),
                                                                                           Double.valueOf(endArea))
                                      .stream()
                                      .findFirst();
        if (!optionalTrade.isPresent()) {
            return Optional.empty();
        }
        OpenApiTradeInfo openApiTradeInfo = optionalTrade.get();
        GuavaBuildingArea guavaBuildingArea = GuavaUtils.getAreaByPrivateArea(guavaBuilding.getAreaList(),
                                                                              String.valueOf(openApiTradeInfo.getArea()));
//        double publicArea = guavaBuildingArea.getPublicArea();
        String marketPrice = null;
        Optional<TradeArticle> tradeArticle = tradeArticleRepository.findByBuildingCodeAndArea1AndArea2(guavaBuilding.getBuildingCode(),
                                                                                                        String.valueOf((int) Math.floor(
                                                                                                            guavaBuildingArea.getPublicArea())),
                                                                                                        String.valueOf((int) Math.floor(
                                                                                                            guavaBuildingArea.getPrivateArea())))
                                                                    .stream()
                                                                    .max(Comparator.comparingInt(TradeArticle::getPrice));
        if (tradeArticle.isPresent()) {
            marketPrice = tradeArticle.get().getTradePrice();
        }
//        List<GuavaTradeResponse> buildingMarketList = this.getBuildingMarketList(String.valueOf(guavaBuilding.getId()),
//                                                                                 0,
//                                                                                 String.valueOf(areaByPrivateArea.getId()));
//        if (buildingMarketList.size() > 0) {
//            marketPrice = buildingMarketList.stream()
//                                            .map(x -> NumberUtils.toInt(x.getPrice()))
//                                            .max(Integer::compareTo)
//                                            .map(String::valueOf)
//                                            .orElse("");
//        }
        String name = String.valueOf((int) (openApiTradeInfo.getArea() * 0.3025));
        if (guavaBuildingArea.getPublicArea() != 0d) {
            name = String.valueOf((int) (guavaBuildingArea.getPublicArea() * 0.3025));
        }
        return Optional.of(GuavaSummaryResponse.builder()
                                               .type(RegionType.BUILDING)
                                               .id(String.valueOf(guavaBuilding.getId()))
                                               .lat(guavaBuilding.getLat())
                                               .lng(guavaBuilding.getLng())
                                               .price(openApiTradeInfo.getSummaryPrice())
                                               .marketPrice(marketPrice)
                                               .name(name)
                                               .build());
    }

    private GuavaSummaryResponse transform(GuavaRegion guavaRegion) {
        int price = 0;
        int marketPrice = 0;
        if (guavaRegion.getRegionType() == RegionType.SIDO) {
            List<GuavaRegionStats> optionalGuavaRegionStats = guavaRegionStatsRepository.findByRegionCodeLike(guavaRegion.getSido() + "%");
            if (optionalGuavaRegionStats.size() > 0) {
                List<Integer> validPrice = optionalGuavaRegionStats.stream()
                                                                   .map(GuavaRegionStats::getPrice)
                                                                   .filter(x -> x != 0)
                                                                   .collect(Collectors.toList());
                List<Integer> validMarketPrice = optionalGuavaRegionStats.stream()
                                                                         .map(GuavaRegionStats::getMarketPrice)
                                                                         .filter(x -> x != 0)
                                                                         .collect(Collectors.toList());
                if (validPrice.size() > 0) {
                    price = validPrice.stream().reduce((a, b) -> a + b).orElse(0) / validPrice.size();
                }
                if (validMarketPrice.size() > 0) {
                    marketPrice = validMarketPrice.stream().reduce((a, b) -> a + b).orElse(0) / validMarketPrice.size();
                }
                if (price + marketPrice == 0) {
                    return null;
                }
            }
        } else if (guavaRegion.getRegionType() == RegionType.SIGUNGU) {
            List<GuavaRegionStats> optionalGuavaRegionStats = guavaRegionStatsRepository.findByRegionCodeLike(guavaRegion.getSigunguCode() +
                                                                                                                  "%");
            if (optionalGuavaRegionStats.size() > 0) {
                List<Integer> validPrice = optionalGuavaRegionStats.stream()
                                                                   .map(GuavaRegionStats::getPrice)
                                                                   .filter(x -> x != 0)
                                                                   .collect(Collectors.toList());
                List<Integer> validMarketPrice = optionalGuavaRegionStats.stream()
                                                                         .map(GuavaRegionStats::getMarketPrice)
                                                                         .filter(x -> x != 0)
                                                                         .collect(Collectors.toList());
                if (validPrice.size() > 0) {
                    price = validPrice.stream().reduce((a, b) -> a + b).orElse(0) / validPrice.size();
                }
                if (validMarketPrice.size() > 0) {
                    marketPrice = validMarketPrice.stream().reduce((a, b) -> a + b).orElse(0) / validMarketPrice.size();
                }
                if (price + marketPrice == 0) {
                    return null;
                }
            }
        } else if (guavaRegion.getRegionType() == RegionType.DONG) {
            Optional<GuavaRegionStats> optionalGuavaRegionStats = guavaRegionStatsRepository.findByRegionCode(guavaRegion.getRegionCode());
            if (optionalGuavaRegionStats.isPresent()) {
                price = optionalGuavaRegionStats.get().getPrice();
                marketPrice = optionalGuavaRegionStats.get().getMarketPrice();
            }
            if (price + marketPrice == 0) {
                return null;
            }
        }

        return GuavaSummaryResponse.builder()
                                   .id(String.valueOf(guavaRegion.getId()))
                                   .lat(guavaRegion.getLat())
                                   .lng(guavaRegion.getLng())
                                   .name(guavaRegion.getDisplayName())
                                   .price(GuavaUtils.getSummaryPrice(String.valueOf(price)))
                                   .marketPrice(GuavaUtils.getSummaryPrice(String.valueOf(marketPrice)))
                                   .build();

    }

    private List<GuavaRegion> searchGuavaRegion(Double lng1, Double lat1, Double lng2, Double lat2) {
        return entityManager.createNativeQuery("SELECT * \n" +
                                                   "FROM guava_region AS r \n" +
                                                   "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + String.format(
            "'LINESTRING(%f %f, %f %f)')",
            lng1,
            lat1,
            lng2,
            lat2) + ", r.point)"
            , GuavaRegion.class).getResultList();
    }

    private List<BuildingMapping> searchGuavaBuilding(Double lng1, Double lat1, Double lng2, Double lat2) {
        return entityManager.createNativeQuery("SELECT * \n" +
                                                   "FROM building_mapping_tb AS r \n" +
                                                   "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + String.format(
            "'LINESTRING(%f %f, %f %f)')",
            lng1,
            lat1,
            lng2,
            lat2) + ", r.point) AND type = 0"
            , BuildingMapping.class).getResultList();
    }

//    private List<GuavaRegion> searchGuavaRegion(Double lng, Double lat) {
//        return entityManager.createNativeQuery(
//            "SELECT *, u_st_distance_sphere(POINT(" + lng + ", " + lat + "), point) AS dist FROM guava_region ORDER BY dist,
// region_code " +
//                "LIMIT 10;"
//            , GuavaRegion.class).getResultList();
//    }
//
//    private List<GuavaRegion> searchGuavaRegion(Double lng, Double lat, Double distance) {
//        // 북동쪽 좌표 구하기
//        Location northEast = GeometryUtils.calculateByDirection(lat, lng, distance, CardinalDirection.NORTHEAST.getBearing());
//
//        // 남서쪽 좌표 구하기
//        Location southWest = GeometryUtils.calculateByDirection(lat, lng, distance, CardinalDirection.SOUTHWEST.getBearing());
//
//        double lng1 = northEast.getLongitude();
//        double lat1 = northEast.getLatitude();
//        double lng2 = southWest.getLongitude();
//        double lat2 = southWest.getLatitude();
//
//        return searchGuavaRegion(lng1, lat1, lng2, lat2);
//    }
}
