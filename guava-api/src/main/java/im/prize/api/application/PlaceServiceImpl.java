package im.prize.api.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.google.common.collect.Lists;
import im.prize.api.application.dto.Location;
import im.prize.api.datatool.AvokadoKakaoClient;
import im.prize.api.datatool.response.AvokadoKakaoAddressResponse;
import im.prize.api.datatool.response.KakaoSearchResponse;
import im.prize.api.domain.oboo.ApartmentMatchTable;
import im.prize.api.domain.oboo.AptArea;
import im.prize.api.domain.oboo.AptInfo;
import im.prize.api.domain.oboo.AptTemp;
import im.prize.api.domain.oboo.OpenApiTradeInfo;
import im.prize.api.domain.oboo.Region;
import im.prize.api.domain.oboo.RegionTemp;
import im.prize.api.domain.oboo.RegionType;
import im.prize.api.domain.oboo.TradeItem;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.ApartmentMatchTableRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.AptAreaRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.AptInfoRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.AptTempRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.OpenApiTradeInfoRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.RegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.RegionTempRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.TradeItemRepository;
import im.prize.api.interfaces.MoneyConverter;
import im.prize.api.interfaces.response.AreaResponse;
import im.prize.api.interfaces.response.GuavaBuildingDetailResponse;
import im.prize.api.interfaces.response.PlaceSummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlaceServiceImpl implements PlaceService {
    @Value("${app.kakao.apiKey}")
    private String kakaoMapApiKey;
    private final ApartmentMatchTableRepository apartmentMatchTableRepository;
    private final OpenApiTradeInfoRepository openApiTradeInfoRepository;
    private final TradeItemRepository tradeItemRepository;
    private final RegionTempRepository regionTempRepository;
    private final ObjectMapper objectMapper;
    private final AptTempRepository aptTempRepository;
    private final RegionRepository regionRepository;
    private final AvokadoKakaoClient avokadoKakaoClient;
    private final EntityManager entityManager;
    private final AptInfoRepository aptInfoRepository;
    private final AptAreaRepository aptAreaRepository;

    public PlaceServiceImpl(ApartmentMatchTableRepository apartmentMatchTableRepository,
                            OpenApiTradeInfoRepository openApiTradeInfoRepository,
                            TradeItemRepository tradeItemRepository,
                            RegionTempRepository regionTempRepository,
                            ObjectMapper objectMapper,
                            AptTempRepository aptTempRepository,
                            RegionRepository regionRepository,
                            AvokadoKakaoClient avokadoKakaoClient,
                            EntityManager entityManager,
                            AptInfoRepository aptInfoRepository,
                            AptAreaRepository aptAreaRepository) {
        this.apartmentMatchTableRepository = apartmentMatchTableRepository;
        this.openApiTradeInfoRepository = openApiTradeInfoRepository;
        this.tradeItemRepository = tradeItemRepository;
        this.regionTempRepository = regionTempRepository;
        this.objectMapper = objectMapper;
        this.aptTempRepository = aptTempRepository;
        this.regionRepository = regionRepository;
        this.avokadoKakaoClient = avokadoKakaoClient;
        this.entityManager = entityManager;
        this.aptInfoRepository = aptInfoRepository;
        this.aptAreaRepository = aptAreaRepository;
    }

    @Override
    public Location getLocation(Double lat, Double lng) {
        Optional<AvokadoKakaoAddressResponse.Document> first = avokadoKakaoClient.address(kakaoMapApiKey, lng, lat)
                                                                                 .getDocuments()
                                                                                 .stream()
                                                                                 .findFirst();
        return first.filter(x -> ObjectUtils.isNotEmpty(x.getAddress()))
                    .map(AvokadoKakaoAddressResponse.Document::getAddress)
                    .map(x -> Location.builder()
                                      .name(x.getRegion1depthName() + " " + x.getRegion2depthName() + " " + x.getRegion3depthName())
                                      .latitude(lat)
                                      .longitude(lng)
                                      .build()).orElse(Location.builder()
                                                               .name("")
                                                               .latitude(lat)
                                                               .longitude(lng)
                                                               .build());
    }

    @Override
    public GuavaBuildingDetailResponse getPlace(String placeId) {
        Optional<ApartmentMatchTable> byId = apartmentMatchTableRepository.findById(Long.valueOf(placeId));
        return this.detailTransform(byId.get());
    }

    @Override
    public List<PlaceSummaryResponse> getPlaces(Double lat, Double lng, Integer level) {
        if (level < 5) {
            List<ApartmentMatchTable> apartmentMatchTables = this.getApartmentMatchTable(lat, lng, 1d);
            return apartmentMatchTables.parallelStream()
                                       .map(this::transform)
                                       .collect(Collectors.toList());
        } else {
            KakaoSearchResponse kakaoSearchResponse = avokadoKakaoClient.coord2regioncode(kakaoMapApiKey,
                                                                                          lng,
                                                                                          lat);
            if (kakaoSearchResponse.getMeta().getTotalCount() == 0) {
                return null;
            }
            KakaoSearchResponse.Document document = kakaoSearchResponse.getDocuments().stream().findFirst().get();
            String dongCode = document.getCode().substring(5, 10);
            String sigunguCode = document.getCode().substring(0, 5);

            List<ApartmentMatchTable> apartmentMatchTables = apartmentMatchTableRepository.findByDongSigunguCode(sigunguCode);
            YearMonth yearMonth = YearMonth.now();
            List<ApartmentMatchTable> collect = apartmentMatchTables.stream()
                                                                    .filter(distinctByKeys(ApartmentMatchTable::getHgnnRegionCode))
                                                                    .collect(Collectors.toList());
            List<PlaceSummaryResponse> collect1 = collect.parallelStream().map(apartmentMatchTable -> {
                Map<String, Object> stringObjectMap =
                    openApiTradeInfoRepository.averageByDongSigunguCodeAndDongCodeAndYearOrderByDateDesc(sigunguCode,
                                                                                                         dongCode,
                                                                                                         String.valueOf(
                                                                                                             yearMonth
                                                                                                                 .getYear()));
                Double avgPrice = (Double) stringObjectMap.getOrDefault("avgPrice", 0d);
                Double maxPrice = (Double) stringObjectMap.getOrDefault("maxPrice", 0d);
                Double minPrice = (Double) stringObjectMap.getOrDefault("minPrice", 0d);
                BigInteger total = (BigInteger) stringObjectMap.getOrDefault("total", 0);

                return PlaceSummaryResponse.builder()
                                           .name(document.getRegion3depthName())
                                           .lat(document.getY())
                                           .lng(document.getX())
                                           .maxTradePrice(MoneyConverter.convertToLandMoneyAbbreviation(String.valueOf(maxPrice.intValue())))
                                           .minTradePrice(MoneyConverter.convertToLandMoneyAbbreviation(String.valueOf(minPrice.intValue())))
                                           .avgTradePrice(MoneyConverter.convertToLandMoneyAbbreviation(String.valueOf(avgPrice.intValue())))
                                           .totalTradeCount(total.toString()).build();
            }).filter(Objects::nonNull).collect(Collectors.toList());
            return collect1;
        }
    }

    private PlaceSummaryResponse transform(ApartmentMatchTable apartmentMatchTable) {
        YearMonth yearMonth = YearMonth.now();
        YearMonth beforeYearMonth = YearMonth.now().minusMonths(1);
        String dongCode = apartmentMatchTable.getHgnnRegionCode().substring(5, 10);
        String sigunguCode = apartmentMatchTable.getHgnnRegionCode().substring(0, 5);
        String aptName = apartmentMatchTable.getAptName();

        // 이전달까지 조회
        List<OpenApiTradeInfo> tradeList =
            openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAptNameAndDateGreaterThan(
                sigunguCode,
                dongCode,
                apartmentMatchTable.getAptName(),
                String.valueOf(yearMonth.getYear()));
//        if (tradeList.size() == 0) {
//            tradeList = openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAptName(sigunguCode,
//                                                                                              dongCode,
//                                                                                              aptName);
//        }
        int newPriceCount = 0;
        Map<String, Object> stringObjectMap =
            openApiTradeInfoRepository.averageByDongSigunguCodeAndDongCodeAndAptNameAndYearOrderByDateDesc(
                sigunguCode,
                dongCode,
                apartmentMatchTable.getAptName(),
                String.valueOf(yearMonth.getYear()));

        Double avgPrice = (Double) stringObjectMap.getOrDefault("avgPrice", 0d);
        Double maxPrice = (Double) stringObjectMap.getOrDefault("maxPrice", 0d);
        Double minPrice = (Double) stringObjectMap.getOrDefault("minPrice", 0d);
        BigInteger total = (BigInteger) stringObjectMap.getOrDefault("total", 0);

        List<TradeItem> naverTradeItemList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(apartmentMatchTable.getPortalId())) {
            naverTradeItemList = tradeItemRepository.findByHcpcNo(apartmentMatchTable.getPortalId())
                                                    .stream()
                                                    .peek(x -> {
                                                        String price = x.getPrcInfo();
                                                        String[] split = price.replace("억", "0000")
                                                                              .replace("만원", "")
                                                                              .replace(",", "")
                                                                              .split(" ");
                                                        int val = 0;
                                                        for (String s : split) {
                                                            val += NumberUtils.toInt(s);
                                                        }
                                                        x.setPrcInfo(Integer.toString(val));
                                                    })
                                                    .sorted(Comparator.comparingInt(p -> Math.abs(Integer.parseInt(p.getPrcInfo()))))
                                                    .collect(
                                                        Collectors.toList());
        }

        String minNaverItemPrice = "";
        String maxNaverItemPrice = "";
        if (naverTradeItemList.size() == 1) {
            minNaverItemPrice = naverTradeItemList.get(0).getPrcInfo();
            maxNaverItemPrice = naverTradeItemList.get(0).getPrcInfo();
        } else if (naverTradeItemList.size() > 1) {
            minNaverItemPrice = naverTradeItemList.get(0).getPrcInfo();
            maxNaverItemPrice = naverTradeItemList.get(naverTradeItemList.size() - 1).getPrcInfo();
        }
        return PlaceSummaryResponse.builder()
                                   .id(apartmentMatchTable.getId())
                                   .name(apartmentMatchTable.getAptName())
                                   .lat(apartmentMatchTable.getLocation().getY())
                                   .lng(apartmentMatchTable.getLocation().getX())
                                   .maxTradePrice(MoneyConverter.convertToLandMoneyAbbreviation(String.valueOf(maxPrice.intValue())))
                                   .minTradePrice(MoneyConverter.convertToLandMoneyAbbreviation(String.valueOf(minPrice.intValue())))
                                   .avgTradePrice(MoneyConverter.convertToLandMoneyAbbreviation(String.valueOf(avgPrice.intValue())))
                                   .totalTradeCount(total.toString())
                                   .minNaverItemPrice(MoneyConverter.convertToLandMoneyAbbreviation(minNaverItemPrice))
                                   .maxNaverItemPrice(MoneyConverter.convertToLandMoneyAbbreviation(maxNaverItemPrice))
                                   .build();
    }

    private Region getRegion(ApartmentMatchTable apartmentMatchTable, Region region) {
        if (region == null) {
            Optional<RegionTemp> byRegionCode = regionTempRepository.findByRegionCode(apartmentMatchTable.getHgnnRegionCode());
            if (byRegionCode.isPresent()) {
                try {
                    JsonNode jsonNode = objectMapper.readValue(byRegionCode.get().getData(), SimpleType.constructUnsafe(JsonNode.class));
                    String regionCode = jsonNode.get("data").get("id").textValue();
                    String fullName = jsonNode.get("data").get("name").textValue();
                    region = new Region();
                    region.setCode(regionCode);
                    region.setFullName(fullName);
                    region.setName(fullName.split(" ")[fullName.split(" ").length - 1]);
                    region.setType(RegionType.DONG);
                    region = regionRepository.save(region);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return region;
    }

    private GuavaBuildingDetailResponse detailTransform(ApartmentMatchTable apartmentMatchTable) {
        Region region = regionRepository.findByCode(apartmentMatchTable.getHgnnRegionCode());
        region = getRegion(apartmentMatchTable, region);
        YearMonth yearMonth = YearMonth.now();
        YearMonth beforeYearMonth = YearMonth.now().minusMonths(1);
        String dongCode = apartmentMatchTable.getHgnnRegionCode().substring(5, 10);
        String sigunguCode = apartmentMatchTable.getHgnnRegionCode().substring(0, 5);
        String aptName = apartmentMatchTable.getAptName();

        // 이전달(-3)까지 포함
        List<OpenApiTradeInfo> tradeList =
            openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAptNameAndDateBetween(
                sigunguCode,
                dongCode,
                aptName,
                beforeYearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")),
                yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")));
        if (tradeList.size() == 0) {
            tradeList = openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAptName(sigunguCode,
                                                                                              dongCode,
                                                                                              aptName);
        }

        Optional<AptInfo> aptInfo = aptInfoRepository.findByAptId(String.valueOf(apartmentMatchTable.getId())).stream().findFirst();
        if (!aptInfo.isPresent()) {
            aptInfo = this.generateAptInfo(apartmentMatchTable);
        }
        return GuavaBuildingDetailResponse.builder()
                                          .id(apartmentMatchTable.getId())
                                          .name(apartmentMatchTable.getAptName())
                                          .address(String.format("%s %s", region.getFullName(), apartmentMatchTable.getLotNumber()))
                                          .dongCount(aptInfo.map(AptInfo::getBuildingCount).orElse(0))
                                          .hoCount(aptInfo.map(AptInfo::getTotalHousehold).orElse(0))
                                          .floorAreaRatio(aptInfo.map(AptInfo::getFloorAreaRatio).orElse(0))
                                          .aptType(aptInfo.map(AptInfo::getAsileType).orElse("-"))
                                          .buildingCoverageRatio(aptInfo.map(AptInfo::getBuildingCoverageRatio).orElse(0))
                                          .buildingCount(aptInfo.map(AptInfo::getBuildingCount).orElse(0))
                                          .maxFloor(aptInfo.map(AptInfo::getFloorMax).orElse(0))
                                          .minFloor(aptInfo.map(AptInfo::getFloorMin).orElse(0))
                                          .since(aptInfo.map(AptInfo::getStartMonth)
                                                .map(x -> x.split("T")[0])
                                                .map(LocalDate::parse)
                                                .map(x -> x.format(DateTimeFormatter.ofPattern("yyyy년 MM월")))
                                                .orElse(""))
                                          .lat(apartmentMatchTable.getLocation().getY())
                                          .lng(apartmentMatchTable.getLocation().getX())
                                          .areaList(aptAreaRepository.findByAptId(String.valueOf(apartmentMatchTable.getId()))
                                                             .stream()
                                                             .map(this::transform)
                                                             .filter(distinctByKeys(AreaResponse::getName))
                                                             .collect(Collectors.toList()))
                                          .build();
    }

    private AreaResponse transform(AptArea aptArea) {
        return AreaResponse.builder()
                           .type(aptArea.getAreaType())
                           .areaId(String.valueOf(aptArea.getId()))
                           .privateArea(aptArea.getPrivateArea())
                           .publicArea(aptArea.getPublicArea())
                           .name(String.format("%s/%s", aptArea.getPrivateArea(), aptArea.getPublicArea()))
                           .hoCount(aptArea.getTotalHousehold())
                           .build();
    }

    private Optional<AptInfo> generateAptInfo(ApartmentMatchTable apartmentMatchTable) {
        Optional<AptTemp> byAptId = aptTempRepository.findByAptId(apartmentMatchTable.getHgnnId())
                                                     .stream()
                                                     .filter(x -> x.getAptId().equals(apartmentMatchTable.getHgnnId()))
                                                     .findFirst();
        try {
            JsonNode jsonNode = objectMapper.readValue(byAptId.get().getData(), SimpleType.constructUnsafe(JsonNode.class));
            JsonNode data = jsonNode.get("result").get("data");
            JsonNode baseInfo = data.get("baseinfo");
            int floorAreaRatio = baseInfo.get("floor_area_ratio") != null ? baseInfo.get("floor_area_ratio").intValue() : 0;
            int buildingCoverageRatio = baseInfo.get("building_coverage_ratio") != null ? baseInfo.get("building_coverage_ratio")
                                                                                                  .intValue() : 0;
            int buildingCount = baseInfo.get("building_count") != null ? baseInfo.get("building_count").intValue() : 0;
            int floorMax = baseInfo.get("floor_max") != null ? baseInfo.get("floor_max").intValue() : 0;
            int floorMin = baseInfo.get("floor_min") != null ? baseInfo.get("floor_min").intValue() : 0;
            String secretKey = baseInfo.get("id") != null ? baseInfo.get("id").textValue() : "";
            int totalHousehold = data.get("total_household") != null ? data.get("total_household").intValue() : 0;
            int parkingInside = baseInfo.get("parking_inside") != null ? baseInfo.get("parking_inside").intValue() : 0;
            int parkingOutside = baseInfo.get("parking_outside") != null ? baseInfo.get("parking_outside").intValue() : 0;
            String heatType = baseInfo.get("heat_type") != null ? baseInfo.get("heat_type").textValue() : "";
            String heatSource = baseInfo.get("heat_source") != null ? baseInfo.get("heat_source").textValue() : "";
            String asileType = baseInfo.get("asile_type") != null ? baseInfo.get("asile_type").textValue() : "";
            String earthQuake = baseInfo.get("earthquake") != null ? baseInfo.get("earthquake").textValue() : "";
            String startMonth = data.get("start_month") != null ? data.get("start_month").textValue() : "1900-01-01T00:00:00.000Z";
            AptInfo aptInfo = AptInfo.builder().floorAreaRatio(floorAreaRatio)
                                     .hgnnId(apartmentMatchTable.getHgnnId())
                                     .portalId(apartmentMatchTable.getPortalId())
                                     .aptId(String.valueOf(apartmentMatchTable.getId()))
                                     .buildingCoverageRatio(buildingCoverageRatio)
                                     .buildingCount(buildingCount)
                                     .floorMax(floorMax)
                                     .floorMin(floorMin)
                                     .secretKey(secretKey)
                                     .totalHousehold(totalHousehold)
                                     .parkingInside(parkingInside)
                                     .parkingOutside(parkingOutside)
                                     .heatType(heatType)
                                     .heatSource(heatSource)
                                     .asileType(asileType)
                                     .earthQuake(earthQuake)
                                     .startMonth(startMonth).build();
            Optional<AptInfo> result = aptInfoRepository.findByAptId(aptInfo.getAptId()).stream().findFirst();
            if (result.isPresent()) {
                return result;
            }
            List<Map<String, Object>> areaList = objectMapper.convertValue(data.get("area"), ArrayList.class);
            List<AptArea> aptAreaList = areaList.stream().map(next -> {
                String privateArea = String.valueOf(next.get("private_area"));
                String publicArea = String.valueOf(next.get("public_area"));
                String areaType = String.valueOf(next.get("area_type"));
                String areaTotalHousehold = String.valueOf(next.get("total_household"));
                return AptArea.builder()
                              .hgnnId(apartmentMatchTable.getHgnnId(

                              ))
                              .portalId(apartmentMatchTable.getPortalId())
                              .aptId(String.valueOf(apartmentMatchTable.getId()))
                              .privateArea(privateArea)
                              .publicArea(publicArea)
                              .areaType(areaType)
                              .totalHousehold(areaTotalHousehold)
                              .build();
            }).collect(Collectors.toList());
            aptAreaRepository.saveAll(aptAreaList);
            return Optional.of(aptInfoRepository.save(aptInfo));

        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    private Point createPoint(double lng, double lat) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPoint(new Coordinate(lng, lat));
    }

    private List<ApartmentMatchTable> getApartmentMatchTable(Double lat, Double lng, Double distance) {
        // 북동쪽 좌표 구하기
        Location northEast = GeometryUtils.calculateByDirection(lat, lng, distance, CardinalDirection.NORTHEAST.getBearing());

        // 남서쪽 좌표 구하기
        Location southWest = GeometryUtils.calculateByDirection(lat, lng, distance, CardinalDirection.SOUTHWEST.getBearing());

        double x1 = northEast.getLongitude();
        double y1 = northEast.getLatitude();
        double x2 = southWest.getLongitude();
        double y2 = southWest.getLatitude();

        return entityManager.createNativeQuery("SELECT * \n" +
                                                   "FROM apartment_match_table AS r \n" +
                                                   "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + String.format(
            "'LINESTRING(%f %f, %f %f)')",
            x1,
            y1,
            x2,
            y2) + ", r.location)"
            , ApartmentMatchTable.class).getResultList();
    }

    private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();
        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                                       .map(ke -> ke.apply(t))
                                       .collect(Collectors.toList());
            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }
}
