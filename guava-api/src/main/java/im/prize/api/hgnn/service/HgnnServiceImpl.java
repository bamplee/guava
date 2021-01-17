package im.prize.api.hgnn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import im.prize.api.application.RegionType;
import im.prize.api.datatool.AvokadoKakaoClient;
import im.prize.api.datatool.response.AvokadoKakaoAddressResponse;
import im.prize.api.domain.oboo.OpenApiRentInfo;
import im.prize.api.domain.oboo.OpenApiTradeInfo;
import im.prize.api.domain.oboo.TradeItem;
import im.prize.api.hgnn.feign.HgnnApiClient;
import im.prize.api.hgnn.feign.HgnnLambdaClient;
import im.prize.api.hgnn.feign.HgnnLambdaRequest;
import im.prize.api.hgnn.feign.NaverLambdaClient;
import im.prize.api.hgnn.feign.NaverLandResponse;
import im.prize.api.hgnn.repository.Building;
import im.prize.api.hgnn.repository.BuildingMapping;
import im.prize.api.hgnn.repository.BuildingMappingRepository;
import im.prize.api.hgnn.repository.BuildingRepository;
import im.prize.api.hgnn.repository.GuavaBuildingAreaRepository;
import im.prize.api.hgnn.repository.GuavaBuildingFailInfo;
import im.prize.api.hgnn.repository.GuavaBuildingFailInfoRepository;
import im.prize.api.hgnn.repository.KaptInfo;
import im.prize.api.hgnn.repository.KaptInfoRepository;
import im.prize.api.hgnn.repository.OpenApiRentBuilding;
import im.prize.api.hgnn.repository.OpenApiRentBuildingRepository;
import im.prize.api.hgnn.repository.OpenApiTradeBuilding;
import im.prize.api.hgnn.repository.OpenApiTradeBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingArea;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaMappingInfo;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaMappingInfoRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionStats;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionStatsRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.ApartmentMatchTableRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.AptTempRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.OpenApiRentInfoRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.OpenApiTradeInfoRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.RegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.RegionTempRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.TradeItemRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.UnmappingTradeList;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.UnmappingTradeListRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class HgnnServiceImpl implements HgnnService {
    @Value("${app.kakao.apiKey}")
    private String kakaoMapApiKey;

    private final HgnnApiClient hgnnApiClient;
    private final RegionRepository regionRepository;
    private final ObjectMapper objectMapper;
    private final RegionTempRepository regionTempRepository;
    private final AptTempRepository aptTempRepository;
    private final ApartmentMatchTableRepository apartmentMatchTableRepository;
    private final HgnnLambdaClient hgnnLambdaClient;
    private final GuavaRegionRepository guavaRegionRepository;
    private final GuavaBuildingRepository guavaBuildingRepository;
    private final GuavaBuildingAreaRepository guavaBuildingAreaRepository;
    private final GuavaBuildingFailInfoRepository guavaBuildingFailInfoRepository;
    private final GuavaMappingInfoRepository guavaMappingInfoRepository;
    private final OpenApiTradeInfoRepository openApiTradeInfoRepository;
    private final TradeItemRepository tradeItemRepository;
    private final GuavaRegionStatsRepository guavaRegionStatsRepository;
    private final NaverLambdaClient naverLambdaClient;
    private final UnmappingTradeListRepository unmappingTradeListRepository;
    private final AvokadoKakaoClient kakaoClient;
    private final OpenApiRentInfoRepository openApiRentInfoRepository;
    private final OpenApiTradeBuildingRepository openApiTradeBuildingRepository;
    private final OpenApiRentBuildingRepository openApiRentBuildingRepository;
    private final BuildingRepository buildingRepository;
    private final KaptInfoRepository kaptInfoRepository;
    private final BuildingMappingRepository buildingMappingRepository;

    public HgnnServiceImpl(HgnnApiClient hgnnApiClient,
                           RegionRepository regionRepository,
                           ObjectMapper objectMapper,
                           RegionTempRepository regionTempRepository,
                           AptTempRepository aptTempRepository,
                           ApartmentMatchTableRepository apartmentMatchTableRepository,
                           HgnnLambdaClient hgnnLambdaClient,
                           GuavaRegionRepository guavaRegionRepository,
                           GuavaBuildingRepository guavaBuildingRepository,
                           GuavaBuildingAreaRepository guavaBuildingAreaRepository,
                           GuavaBuildingFailInfoRepository guavaBuildingFailInfoRepository,
                           GuavaMappingInfoRepository guavaMappingInfoRepository,
                           OpenApiTradeInfoRepository openApiTradeInfoRepository,
                           TradeItemRepository tradeItemRepository,
                           GuavaRegionStatsRepository guavaRegionStatsRepository,
                           NaverLambdaClient naverLambdaClient,
                           UnmappingTradeListRepository unmappingTradeListRepository,
                           AvokadoKakaoClient kakaoClient,
                           OpenApiRentInfoRepository openApiRentInfoRepository,
                           OpenApiTradeBuildingRepository openApiTradeBuildingRepository,
                           OpenApiRentBuildingRepository openApiRentBuildingRepository,
                           BuildingRepository buildingRepository,
                           KaptInfoRepository kaptInfoRepository,
                           BuildingMappingRepository buildingMappingRepository) {
        this.hgnnApiClient = hgnnApiClient;
        this.regionRepository = regionRepository;
        this.objectMapper = objectMapper;
        this.regionTempRepository = regionTempRepository;
        this.aptTempRepository = aptTempRepository;
        this.apartmentMatchTableRepository = apartmentMatchTableRepository;
        this.hgnnLambdaClient = hgnnLambdaClient;
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
        this.guavaBuildingFailInfoRepository = guavaBuildingFailInfoRepository;
        this.guavaMappingInfoRepository = guavaMappingInfoRepository;
        this.openApiTradeInfoRepository = openApiTradeInfoRepository;
        this.tradeItemRepository = tradeItemRepository;
        this.guavaRegionStatsRepository = guavaRegionStatsRepository;
        this.naverLambdaClient = naverLambdaClient;
        this.unmappingTradeListRepository = unmappingTradeListRepository;
        this.kakaoClient = kakaoClient;
        this.openApiRentInfoRepository = openApiRentInfoRepository;
        this.openApiTradeBuildingRepository = openApiTradeBuildingRepository;
        this.openApiRentBuildingRepository = openApiRentBuildingRepository;
        this.buildingRepository = buildingRepository;
        this.kaptInfoRepository = kaptInfoRepository;
        this.buildingMappingRepository = buildingMappingRepository;
    }

    @Override
    public void sync() {
        List<GuavaRegion> result = guavaRegionRepository.findAll()
                                                        .parallelStream()
                                                        .filter(x -> x.getRegionType() == RegionType.SIGUNGU)
                                                        .peek(guavaRegion -> {
                                                            log.info("i : {}, {}", guavaRegion.getId(), guavaRegion.getName());
                                                            List<GuavaBuilding> byRegionCode = guavaBuildingRepository.findByRegionCodeLike(
                                                                guavaRegion.getSido() + guavaRegion.getSigungu() + "%");
                                                            boolean isActive = byRegionCode.size() > 0;
                                                            if (isActive) {
                                                                guavaRegion.setIsActive(true);
                                                            }
                                                        })
                                                        .collect(Collectors.toList());

        Lists.partition(result, 1000).forEach(x -> {
            List<GuavaRegion> guavaRegionList = guavaRegionRepository.saveAll(x);
            log.info("save : {} / {}", guavaRegionList.size(), result.size());
        });
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 100);
        Page<GuavaMappingInfo> all = guavaMappingInfoRepository.findByType(0, pageRequest);

//        List<GuavaMappingInfo> collect = guavaMappingInfoRepository.findByBuildingCodeIn(Lists.newArrayList("5ds32"))
//                                                                   .stream()
//                                                                   .collect(Collectors.toList());
//        for (GuavaMappingInfo guavaMappingInfo : collect) {
//            List<OpenApiTradeInfo> byRegionIdAndAptNameAndLotNumber =
//                openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAptNameAndLotNumberAndBuildingIdIsNull(
//                    guavaMappingInfo.getSigunguCode(),
//                    guavaMappingInfo.getDongCode(),
//                    guavaMappingInfo.getName(),
//                    guavaMappingInfo.getLotNumber()).stream().peek(x -> x.setRegionCode(guavaMappingInfo.getRegionCode())).peek(x
//                                                                                                                                    -> x
//                    .setBuildingId(guavaMappingInfo.getBuildingCode())).collect(Collectors.toList());
//            if (byRegionIdAndAptNameAndLotNumber.size() > 0) {
//                log.info("code: {}, size: {}", guavaMappingInfo.getBuildingCode(), byRegionIdAndAptNameAndLotNumber.size());
//                openApiTradeInfoRepository.saveAll(byRegionIdAndAptNameAndLotNumber);
//            }
//        }

//        while (all.getTotalPages() > page) {
//            all = guavaMappingInfoRepository.findByType(0, PageRequest.of(page++, 100));
//            for (GuavaMappingInfo guavaMappingInfo : all) {
//                List<OpenApiTradeInfo> byRegionIdAndAptNameAndLotNumber =
//                    openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAptNameAndLotNumberAndBuildingIdIsNull(
//                        guavaMappingInfo.getSigunguCode(),
//                        guavaMappingInfo.getDongCode(),
//                        guavaMappingInfo.getName(),
//                        guavaMappingInfo.getLotNumber()).stream().peek(x -> x.setRegionCode(guavaMappingInfo.getRegionCode())).peek(x
//
// -> x
//                        .setBuildingId(guavaMappingInfo.getBuildingCode())).collect(Collectors.toList());
//                if (byRegionIdAndAptNameAndLotNumber.size() > 0) {
//                    log.info("code: {}, size: {}", guavaMappingInfo.getBuildingCode(), byRegionIdAndAptNameAndLotNumber.size());
//                    openApiTradeInfoRepository.saveAll(byRegionIdAndAptNameAndLotNumber);
//                }
//            }
//        }
//        int page = 0;
//        PageRequest pageRequest = PageRequest.of(page, 100);
//        Page<GuavaBuilding> all = guavaBuildingRepository.findAll(pageRequest);
//        while (all.getTotalPages() > page) {
//            pageRequest = PageRequest.of(page, 100);
//            List<GuavaBuilding> result = guavaBuildingRepository.findAll(pageRequest).getContent().parallelStream().map(x -> {
//                GuavaHgnnBuilding guavaHgnnBuilding = this.executor(x.getBuildingCode());
//                if(guavaHgnnBuilding == null) {
//                    return x;
//                }
//                int totalHousehold = 0;
//                int baseInfoTotalHousehold = 0;
//                int baseInfoTotalHouseholdCounted = 0;
//                if (guavaHgnnBuilding.getTotalHousehold() != null) {
//                    totalHousehold = guavaHgnnBuilding.getTotalHousehold();
//                }
//                GuavaHgnnBaseInfo baseinfo = guavaHgnnBuilding.getBaseinfo();
//                if (baseinfo != null) {
//                    if (baseinfo.getTotalHousehold() != null) {
//                        baseInfoTotalHousehold = baseinfo.getTotalHousehold();
//                    }
//                    if (baseinfo.getTotalHouseholdCounted() != null) {
//                        baseInfoTotalHouseholdCounted = baseinfo.getTotalHouseholdCounted();
//                    }
//                }
//                totalHousehold = totalHousehold > baseInfoTotalHousehold ? totalHousehold : baseInfoTotalHousehold;
//                x.setTotalHousehold(totalHousehold);
//                x.setTotalHouseholdCounted(baseInfoTotalHouseholdCounted);
//                return x;
//            }).collect(Collectors.toList());
//            List<GuavaBuilding> guavaBuildings = guavaBuildingRepository.saveAll(result);
//            log.info("save size : {}, page size : {}/{}", guavaBuildings.size(), page, all.getTotalPages());
//            page++;
//        }
//        List<GuavaRegion> guavaRegionList = guavaRegionRepository.findAll();
//        List<String> buildingList = guavaRegionList.stream()
//                                                   .map(x -> Lists.newArrayList(x.getBuildingIdCsv().split(",")))
//                                                   .flatMap(Collection::stream)
//                                                   .filter(StringUtils::isNotEmpty)
//                                                   .collect(Collectors.toList());
//        log.info("building size : {}", buildingList.size());
//        List<String> existBuildingCodes = guavaBuildingRepository.findAll().stream().map(GuavaBuilding::getBuildingCode).collect
// (Collectors.toList());
//        List<List<String>> buildingPartitionList = Lists.partition(buildingList.stream().filter(x -> !existBuildingCodes.contains(x))
// .collect(Collectors.toList()), 100);
//        for (int i = 0; i < buildingPartitionList.size(); i++) {
//            log.info("{} / {}", i, buildingPartitionList.size());
//            List<String> strings = buildingPartitionList.get(i);
//            List<GuavaBuilding> collect = strings.parallelStream()
//                                                 .map(this::executor)
//                                                 .filter(ObjectUtils::isNotEmpty)
//                                                 .map(this::transform)
//                                                 .collect(Collectors.toList());
////            List<GuavaBuilding> guavaBuildings1 = guavaBuildingRepository.saveAll(collect);
////            log.info("success, {}", guavaBuildings1.size());
//        }
    }

    @Override
    public void sync2() {
        List<GuavaRegionStats> collect = guavaRegionRepository.findAll()
                                                              .parallelStream()
                                                              .map(this::transform)
                                                              .filter(ObjectUtils::isNotEmpty)
                                                              .collect(Collectors.toList());
        List<List<GuavaRegionStats>> partition = Lists.partition(collect, 1000);
        for (int i = 0; i < partition.size(); i++) {
            List<GuavaRegionStats> guavaRegionStats = partition.get(i);
            log.info("save index {} / {}", i, partition.size());
            guavaRegionStatsRepository.saveAll(guavaRegionStats);
        }
    }

    @Override
    public void sync3() {
/*
        List<GuavaMappingInfo> seoulList = guavaMappingInfoRepository.findByRegionCodeStartsWithAndPortalIdIsNotNull("11");
        List<GuavaMappingInfo> gyunggiList = guavaMappingInfoRepository.findByRegionCodeStartsWithAndPortalIdIsNotNull("41");
        List<GuavaMappingInfo> uniqueList = Stream.concat(seoulList.stream(), gyunggiList.stream()).collect(Collectors.toList());
*/
        List<GuavaMappingInfo> uniqueList = guavaMappingInfoRepository.findByRegionCodeStartsWithAndPortalIdIsNotNull("11");

        List<String> validPortalIdList = uniqueList.stream()
                                                   .map(GuavaMappingInfo::getPortalId)
                                                   .map(Object::toString)
                                                   .collect(Collectors.toList());
        List<TradeItem> beforeList = tradeItemRepository.findByEndDateIsNull()
                                                        .stream()
                                                        .filter(x -> validPortalIdList.contains(x.getHcpcNo()))
                                                        .collect(Collectors.toList());
        List<TradeItem> svcList = uniqueList.stream()
                                            .map(x -> {
                                                int page = 1;
                                                String portalId = String.valueOf(x.getPortalId());
                                                List<TradeItem> result = Lists.newArrayList();
                                                try {
                                                    Thread.sleep(2000);
                                                    NaverLandResponse complexArticleList = naverLambdaClient.getLandDetail(portalId, page++)
                                                                                                            .getData();
                                                    result.addAll(complexArticleList.getResult().getList()
                                                                                    .stream()
                                                                                    .map(article -> this.transform(
                                                                                        portalId,
                                                                                        article))
                                                                                    .collect(Collectors.toList()));
                                                    while ("Y".equals(complexArticleList.getResult().getMoreDataYn())) {
                                                        Thread.sleep(2000);
                                                        complexArticleList = naverLambdaClient.getLandDetail(portalId, page++).getData();
                                                        result.addAll(complexArticleList.getResult().getList()
                                                                                        .stream()
                                                                                        .map(article -> this.transform(
                                                                                            portalId,
                                                                                            article))
                                                                                        .collect(Collectors.toList()));
                                                    }
                                                } catch (Exception e) {
                                                    log.error(e.getMessage());
                                                }
                                                log.info("portalId : {}, size : {}", portalId, result.size());
                                                return result;
                                            }).flatMap(Collection::stream).collect(Collectors.toList());

        List<String> beforeAtclNoList = beforeList.stream().map(TradeItem::getAtclNo).collect(Collectors.toList());
        List<String> svcAtclNoList = svcList.stream().map(TradeItem::getAtclNo).collect(Collectors.toList());

        // 종료처리
        List<TradeItem> beforeResult = beforeList.stream()
                                                 .filter(x -> !svcAtclNoList.contains(x.getAtclNo()))
                                                 .peek(x -> x.setEndDate(LocalDateTime.now()
                                                                                      .format(DateTimeFormatter.ofPattern("yyyyMMdd"))))
                                                 .collect(Collectors.toList());
        // 신규 리스트
        List<TradeItem> afterResult = svcList.stream()
                                             .filter(x -> !beforeAtclNoList.contains(x.getAtclNo()))
                                             .collect(Collectors.toList());
        // 결과
        List<TradeItem> tradeItems = Stream.concat(beforeResult.stream(), afterResult.stream()).collect(Collectors.toList());
        List<List<TradeItem>> beforePartition = Lists.partition(tradeItems, 100);
        for (int i = 0; i < beforePartition.size(); i++) {
            List<TradeItem> items = beforePartition.get(i);
            log.info("result.. : {}/{}", i, tradeItems.size());
            tradeItemRepository.saveAll(items);
        }
    }

    @Override
    public void sync4() {
        int page = 0;
        Page<UnmappingTradeList> all = unmappingTradeListRepository.findByBuildingIdIsNull(PageRequest.of(0, 100));
        List<GuavaMappingInfo> result = Lists.newArrayList();
        while (all.getTotalPages() > page) {
            log.info("page : {}", page);
            all = unmappingTradeListRepository.findByBuildingIdIsNull(PageRequest.of(page++, 100));
            for (UnmappingTradeList unmappingTradeList : all) {
                String regionCode = unmappingTradeList.getDongSigunguCode() + unmappingTradeList.getDongCode();
                Optional<GuavaRegion> byCode = guavaRegionRepository.findByRegionCode(regionCode);
                if (byCode.isPresent()) {
                    GuavaRegion guavaRegion = byCode.get();
                    List<String> buildingIdList = Lists.newArrayList(guavaRegion.getBuildingIdCsv().split(","));
                    GuavaBuilding guavaBuilding = new GuavaBuilding();
                    for (String buildingId : buildingIdList) {
                        Optional<GuavaBuilding> byBuildingCode = guavaBuildingRepository.findByBuildingCode(buildingId);
                        if(byBuildingCode.isPresent()) {
                            GuavaBuilding building = byBuildingCode.get();
                            if (unmappingTradeList.getAptName().replace("(", "").replace(")", "").equals(building.getName())) {
                                guavaBuilding = building;
                            }
                        }
                    }
                    if (guavaBuilding.getName() != null && guavaBuilding.getAddress() != null) {
                        unmappingTradeList.setBuildingId(guavaBuilding.getBuildingCode());
                        guavaMappingInfoRepository.save(GuavaMappingInfo.builder()
                                                                        .type(0)
                                                                        .name(unmappingTradeList.getAptName())
                                                                        .address(guavaBuilding.getAddress())
                                                                        .tradeRegionCode(unmappingTradeList.getRegionCode())
                                                                        .regionCode(guavaRegion.getRegionCode())
                                                                        .buildingCode(guavaBuilding.getBuildingCode())
                                                                        .portalId(String.valueOf(guavaBuilding.getPortalId()))
                                                                        .lotNumber(unmappingTradeList.getLotNumber())
                                                                        .build());
                        unmappingTradeListRepository.save(unmappingTradeList);
                        log.info("\n{}///{}\n{}///{}\n",
                                 unmappingTradeList.getAptName(),
                                 guavaBuilding.getName(),
                                 guavaRegion.getName() + " " + unmappingTradeList.getLotNumber(),
                                 guavaBuilding.getAddress());
                    }
                } else {
                    AvokadoKakaoAddressResponse nameSearch = kakaoClient.search(kakaoMapApiKey, unmappingTradeList.getAptName(), 1, 30);
                    AvokadoKakaoAddressResponse dongSearch = kakaoClient.search(kakaoMapApiKey, unmappingTradeList.getDong(), 1, 30);
                    log.error("error : {}", regionCode);
                }
            }
        }
    }

    @Override
    public void sync5() {
        int page = 0;
        Page<UnmappingTradeList> all = unmappingTradeListRepository.findByBuildingIdIsNull(PageRequest.of(0, 100));
        List<GuavaMappingInfo> result = Lists.newArrayList();
        while (all.getTotalPages() > page) {
            log.info("page : {}", page);
            all = unmappingTradeListRepository.findByBuildingIdIsNull(PageRequest.of(page++, 100));
            for (UnmappingTradeList unmappingTradeList : all) {
                String regionCode = unmappingTradeList.getDongSigunguCode() + unmappingTradeList.getDongCode();
                Optional<GuavaRegion> byCode = guavaRegionRepository.findByRegionCode(regionCode);
                if (byCode.isPresent()) {
                    GuavaRegion guavaRegion = byCode.get();
                    GuavaBuilding guavaBuilding = new GuavaBuilding();

                    List<GuavaBuilding> byAddress = guavaBuildingRepository.findByAddressLike(guavaRegion.getName() + "%");
                    System.out.println();
//                    if (guavaBuilding.getName() != null && guavaBuilding.getAddress() != null) {
//                        unmappingTradeList.setBuildingId(guavaBuilding.getBuildingCode());
//                        guavaMappingInfoRepository.save(GuavaMappingInfo.builder()
//                                                                        .gwanboType(0)
//                                                                        .name(unmappingTradeList.getAptName())
//                                                                        .address(guavaBuilding.getAddress())
//                                                                        .tradeRegionCode(unmappingTradeList.getRegionCode())
//                                                                        .regionCode(guavaRegion.getRegionCode())
//                                                                        .buildingCode(guavaBuilding.getBuildingCode())
//                                                                        .portalId(String.valueOf(guavaBuilding.getPortalId()))
//                                                                        .lotNumber(unmappingTradeList.getLotNumber())
//                                                                        .build());
//                        unmappingTradeListRepository.save(unmappingTradeList);
//                        log.info("\n{}///{}\n{}///{}\n",
//                                 unmappingTradeList.getAptName(),
//                                 guavaBuilding.getName(),
//                                 guavaRegion.getName() + " " + unmappingTradeList.getLotNumber(),
//                                 guavaBuilding.getAddress());
//                    }
                } else {
                    AvokadoKakaoAddressResponse nameSearch = kakaoClient.search(kakaoMapApiKey, unmappingTradeList.getAptName(), 1, 30);
                    AvokadoKakaoAddressResponse dongSearch = kakaoClient.search(kakaoMapApiKey, unmappingTradeList.getDong(), 1, 30);
                    log.error("error : {}", regionCode);
                }
            }
        }
    }

    @Override
    public void sync6() {
        List<GuavaBuilding> byPointIsNull = guavaBuildingRepository.findByPointIsNull();
        List<GuavaBuilding> collect = byPointIsNull.stream().map(x -> {
            AvokadoKakaoAddressResponse search = kakaoClient.search(kakaoMapApiKey, x.getAddress(), 1, 30);
            if (search.getMeta().getTotalCount() > 0) {
                AvokadoKakaoAddressResponse.Document document = search.getDocuments().get(0);
                x.setLat(document.getY());
                x.setLng(document.getX());
                x.setPoint(createPoint(x.getLng(), x.getLat()));
            }
            return x;
        }).collect(Collectors.toList());
        guavaBuildingRepository.saveAll(collect);
        System.out.println();

    }

    @Override
    public void sync7() {
        List<GuavaMappingInfo> guavaMappingInfos = guavaMappingInfoRepository.findByIdGreaterThanEqual(9472045l);
        for (GuavaMappingInfo guavaMappingInfo : guavaMappingInfos) {
            List<OpenApiTradeInfo> byRegionIdAndAptNameAndLotNumber =
                openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAptNameAndLotNumberAndBuildingIdIsNull(
                    guavaMappingInfo.getSigunguCode(),
                    guavaMappingInfo.getDongCode(),
                    guavaMappingInfo.getName(),
                    guavaMappingInfo.getLotNumber())
                                          .stream()
                                          .peek(x -> x.setRegionCode(guavaMappingInfo.getRegionCode()))
                                          .peek(x -> x.setBuildingId(guavaMappingInfo.getBuildingCode()))
                                          .collect(Collectors.toList());
            if (byRegionIdAndAptNameAndLotNumber.size() > 0) {
                log.info("code: {}, size: {}", guavaMappingInfo.getBuildingCode(), byRegionIdAndAptNameAndLotNumber.size());
                openApiTradeInfoRepository.saveAll(byRegionIdAndAptNameAndLotNumber);
            }
        }

//        while (all.getTotalPages() > page) {
//            all = guavaMappingInfoRepository.findByType(0, PageRequest.of(page++, 100));
//            for (GuavaMappingInfo guavaMappingInfo : all) {
//                List<OpenApiTradeInfo> byRegionIdAndAptNameAndLotNumber =
//                    openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAptNameAndLotNumberAndBuildingIdIsNull(
//                        guavaMappingInfo.getSigunguCode(),
//                        guavaMappingInfo.getDongCode(),
//                        guavaMappingInfo.getName(),
//                        guavaMappingInfo.getLotNumber()).stream().peek(x -> x.setRegionCode(guavaMappingInfo.getRegionCode())).peek(x
//
// -> x
//                        .setBuildingId(guavaMappingInfo.getBuildingCode())).collect(Collectors.toList());
//                if (byRegionIdAndAptNameAndLotNumber.size() > 0) {
//                    log.info("code: {}, size: {}", guavaMappingInfo.getBuildingCode(), byRegionIdAndAptNameAndLotNumber.size());
//                    openApiTradeInfoRepository.saveAll(byRegionIdAndAptNameAndLotNumber);
//                }
//            }
//        }
//        int page = 0;
//        PageRequest pageRequest = PageRequest.of(page, 100);
//        Page<GuavaBuilding> all = guavaBuildingRepository.findAll(pageRequest);
//        while (all.getTotalPages() > page) {
//            pageRequest = PageRequest.of(page, 100);
//            List<GuavaBuilding> result = guavaBuildingRepository.findAll(pageRequest).getContent().parallelStream().map(x -> {
//                GuavaHgnnBuilding guavaHgnnBuilding = this.executor(x.getBuildingCode());
//                if(guavaHgnnBuilding == null) {
//                    return x;
//                }
//                int totalHousehold = 0;
//                int baseInfoTotalHousehold = 0;
//                int baseInfoTotalHouseholdCounted = 0;
//                if (guavaHgnnBuilding.getTotalHousehold() != null) {
//                    totalHousehold = guavaHgnnBuilding.getTotalHousehold();
//                }
//                GuavaHgnnBaseInfo baseinfo = guavaHgnnBuilding.getBaseinfo();
//                if (baseinfo != null) {
//                    if (baseinfo.getTotalHousehold() != null) {
//                        baseInfoTotalHousehold = baseinfo.getTotalHousehold();
//                    }
//                    if (baseinfo.getTotalHouseholdCounted() != null) {
//                        baseInfoTotalHouseholdCounted = baseinfo.getTotalHouseholdCounted();
//                    }
//                }
//                totalHousehold = totalHousehold > baseInfoTotalHousehold ? totalHousehold : baseInfoTotalHousehold;
//                x.setTotalHousehold(totalHousehold);
//                x.setTotalHouseholdCounted(baseInfoTotalHouseholdCounted);
//                return x;
//            }).collect(Collectors.toList());
//            List<GuavaBuilding> guavaBuildings = guavaBuildingRepository.saveAll(result);
//            log.info("save size : {}, page size : {}/{}", guavaBuildings.size(), page, all.getTotalPages());
//            page++;
//        }
//        List<GuavaRegion> guavaRegionList = guavaRegionRepository.findAll();
//        List<String> buildingList = guavaRegionList.stream()
//                                                   .map(x -> Lists.newArrayList(x.getBuildingIdCsv().split(",")))
//                                                   .flatMap(Collection::stream)
//                                                   .filter(StringUtils::isNotEmpty)
//                                                   .collect(Collectors.toList());
//        log.info("building size : {}", buildingList.size());
//        List<String> existBuildingCodes = guavaBuildingRepository.findAll().stream().map(GuavaBuilding::getBuildingCode).collect
// (Collectors.toList());
//        List<List<String>> buildingPartitionList = Lists.partition(buildingList.stream().filter(x -> !existBuildingCodes.contains(x))
// .collect(Collectors.toList()), 100);
//        for (int i = 0; i < buildingPartitionList.size(); i++) {
//            log.info("{} / {}", i, buildingPartitionList.size());
//            List<String> strings = buildingPartitionList.get(i);
//            List<GuavaBuilding> collect = strings.parallelStream()
//                                                 .map(this::executor)
//                                                 .filter(ObjectUtils::isNotEmpty)
//                                                 .map(this::transform)
//                                                 .collect(Collectors.toList());
////            List<GuavaBuilding> guavaBuildings1 = guavaBuildingRepository.saveAll(collect);
////            log.info("success, {}", guavaBuildings1.size());
//        }
    }

    @Override
    public void sync8() {
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 100);
        Page<GuavaMappingInfo> guavaMappingInfos = guavaMappingInfoRepository.findByType(0, pageRequest);
        int totalPages = guavaMappingInfos.getTotalPages();
        while (page <= totalPages) {
            guavaMappingInfos = guavaMappingInfoRepository.findByType(0, PageRequest.of(page, 100));
            for (GuavaMappingInfo guavaMappingInfo : guavaMappingInfos) {
                Optional<GuavaRegion> byRegionCode = guavaRegionRepository.findByRegionCode(guavaMappingInfo.getRegionCode());
                if (byRegionCode.isPresent()) {
                    GuavaRegion guavaRegion = byRegionCode.get();
                    List<OpenApiRentInfo> byRegionCodeAndLotNumberAndAptName =
                        openApiRentInfoRepository.findByRegionCodeAndDongAndLotNumberAndAptName(
                            guavaRegion.getRegionCode().substring(0, 5),
                            guavaRegion.getDongName(),
                            guavaMappingInfo.getLotNumber(),
                            guavaMappingInfo.getName()).stream().filter(x -> x.getBuildingId() == null).peek(openApiRentInfo -> {
                            String month = openApiRentInfo.getMonth().length() == 1 ? String.format("0%s",
                                                                                                    openApiRentInfo.getMonth()) :
                                openApiRentInfo
                                    .getMonth();
                            String day = openApiRentInfo.getDay().length() == 1 ? String.format("0%s",
                                                                                                openApiRentInfo.getDay()) :
                                openApiRentInfo.getDay();
                            String date = openApiRentInfo.getYear() + month + day;
                            openApiRentInfo.setDate(date);
                            openApiRentInfo.setRegionCode(guavaRegion.getRegionCode());
                            openApiRentInfo.setBuildingId(guavaMappingInfo.getBuildingCode());
                        }).collect(Collectors.toList());
                    if (byRegionCodeAndLotNumberAndAptName.size() > 0) {
                        log.info("[{}/{}] code: {}, size: {}",
                                 page,
                                 totalPages,
                                 guavaMappingInfo.getBuildingCode(),
                                 byRegionCodeAndLotNumberAndAptName.size());
                        openApiRentInfoRepository.saveAll(byRegionCodeAndLotNumberAndAptName);
                    }
                }
            }
            page++;
        }
    }

    @Override
    public void trade() {
        List<OpenApiTradeBuilding> all = openApiTradeBuildingRepository.findByRegionCodeIsNull();

        List<OpenApiTradeBuilding> result = Lists.newArrayList();
        for (OpenApiTradeBuilding openApiTradeBuilding : all) {
            log.info("id : {}", openApiTradeBuilding.getId());
            String regionCode = openApiTradeBuilding.getSigunguCode() + openApiTradeBuilding.getDongCode();
            Optional<GuavaRegion> byRegionCode =
                guavaRegionRepository.findByRegionCode(regionCode);
            if (byRegionCode.isPresent()) {
                GuavaRegion guavaRegion = byRegionCode.get();
                openApiTradeBuilding.setRegionCode(guavaRegion.getRegionCode());
                result.add(openApiTradeBuilding);
            }
        }
        Lists.partition(result, 1000).forEach(x -> {
            openApiTradeBuildingRepository.saveAll(x);
            log.info("save : {}", x.size());
        });
    }

    @Override
    public void rent() {
        List<OpenApiRentBuilding> all = openApiRentBuildingRepository.findByRegionCodeIsNull();

        List<OpenApiRentBuilding> result = Lists.newArrayList();
        for (OpenApiRentBuilding openApiRentBuilding : all) {
            log.info("id : {}", openApiRentBuilding.getId());
            try {
                if (openApiRentBuilding.getDong().contains("1가동") ||
                    openApiRentBuilding.getDong().contains("2가동") ||
                    openApiRentBuilding.getDong().contains("3가동")) {
                    openApiRentBuilding.setDong(openApiRentBuilding.getDong().replace("가동", "가"));
                }

                Optional<GuavaRegion> byRegionCode = Optional.empty();
//            String regionCode = openApiRentBuilding.getSigunguCode() + openApiRentBuilding.getDongCode();
                byRegionCode =
                    guavaRegionRepository.findBySidoAndSigunguAndDongNameAndIsActive(openApiRentBuilding.getSigunguCode().substring(0, 2),
                                                                                     openApiRentBuilding.getSigunguCode().substring(2, 5),
                                                                                     openApiRentBuilding.getDong(),
                                                                                     true);
                if (!byRegionCode.isPresent()) {
                    byRegionCode =
                        guavaRegionRepository.findBySidoAndSigunguAndDongName(openApiRentBuilding.getSigunguCode().substring(0, 2),
                                                                              openApiRentBuilding.getSigunguCode().substring(2, 5),
                                                                              openApiRentBuilding.getDong());
                }
                if (!byRegionCode.isPresent()) {
                    byRegionCode =
                        guavaRegionRepository.findBySidoAndSigunguAndRiNameAndIsActive(openApiRentBuilding.getSigunguCode().substring(0, 2),
                                                                                       openApiRentBuilding.getSigunguCode().substring(2, 5),
                                                                                       openApiRentBuilding.getDong(),
                                                                                       null);
                }
                if (!byRegionCode.isPresent()) {
                    byRegionCode =
                        guavaRegionRepository.findBySidoAndSigunguAndRiNameAndIsActive(openApiRentBuilding.getSigunguCode().substring(0, 2),
                                                                                       openApiRentBuilding.getSigunguCode().substring(2, 5),
                                                                                       openApiRentBuilding.getDong(), true);
                }
                if (!byRegionCode.isPresent()) {
                    if (openApiRentBuilding.getDong().indexOf("읍") == 2 && openApiRentBuilding.getDong().contains("리")) {
                        String[] splitStrings = openApiRentBuilding.getDong().split("읍");
//                    splitStrings[0] = splitStrings[0] + "읍";
                        splitStrings[1] = splitStrings[1];
                        byRegionCode =
                            guavaRegionRepository.findBySidoAndSigunguAndDongNameAndRiNameAndIsActive(openApiRentBuilding.getSigunguCode()
                                                                                                                         .substring(0,
                                                                                                                                    2),
                                                                                                      openApiRentBuilding.getSigunguCode()
                                                                                                                         .substring(2,
                                                                                                                                    5),
                                                                                                      splitStrings[0] + "읍",
                                                                                                      splitStrings[1],
                                                                                                      true);
                        if (!byRegionCode.isPresent()) {
                            byRegionCode =
                                guavaRegionRepository.findBySidoAndSigunguAndDongNameAndRiName(openApiRentBuilding.getSigunguCode()
                                                                                                                  .substring(0,
                                                                                                                             2),
                                                                                               openApiRentBuilding.getSigunguCode()
                                                                                                                  .substring(2,
                                                                                                                             5),
                                                                                               splitStrings[0] + "면",
                                                                                               splitStrings[1]);
                        }
                        if (!byRegionCode.isPresent()) {
                            byRegionCode =
                                guavaRegionRepository.findBySidoAndSigunguAndDongNameAndRiName(openApiRentBuilding.getSigunguCode()
                                                                                                                  .substring(0,
                                                                                                                             2),
                                                                                               openApiRentBuilding.getSigunguCode()
                                                                                                                  .substring(2,
                                                                                                                             5),
                                                                                               splitStrings[0] + "읍",
                                                                                               splitStrings[1]);
                        }
                        if (!byRegionCode.isPresent()) {
                            byRegionCode =
                                guavaRegionRepository.findBySidoAndSigunguAndSigunguNameAndDongName(openApiRentBuilding.getSigunguCode()
                                                                                                                       .substring(0,
                                                                                                                                  2),
                                                                                                    openApiRentBuilding.getSigunguCode()
                                                                                                                       .substring(2,
                                                                                                                                  5),
                                                                                                    splitStrings[0] + "면",
                                                                                                    splitStrings[1]);
                        }
                        if (!byRegionCode.isPresent()) {
                            byRegionCode =
                                guavaRegionRepository.findBySidoAndSigunguAndSigunguNameAndDongName(openApiRentBuilding.getSigunguCode()
                                                                                                                       .substring(0,
                                                                                                                                  2),
                                                                                                    openApiRentBuilding.getSigunguCode()
                                                                                                                       .substring(2,
                                                                                                                                  5),
                                                                                                    splitStrings[0] + "읍",
                                                                                                    splitStrings[1]);
                        }
                        if (!byRegionCode.isPresent()) {
                            byRegionCode = guavaRegionRepository.findBySidoAndSigunguAndNameContains(openApiRentBuilding.getSigunguCode()
                                                                                                                        .substring(0, 2),
                                                                                                     openApiRentBuilding.getSigunguCode()
                                                                                                                        .substring(2, 5),
                                                                                                     splitStrings[0] + "면 " + splitStrings[1]);
                        }
                        if (!byRegionCode.isPresent()) {
                            byRegionCode = guavaRegionRepository.findBySidoAndSigunguAndNameContains(openApiRentBuilding.getSigunguCode()
                                                                                                                        .substring(0, 2),
                                                                                                     openApiRentBuilding.getSigunguCode()
                                                                                                                        .substring(2, 5),
                                                                                                     splitStrings[0] + "읍 " + splitStrings[1]);
                        }
                    } else if (openApiRentBuilding.getDong().indexOf("면") == 2 && openApiRentBuilding.getDong().contains("리")) {
                        String[] splitStrings = openApiRentBuilding.getDong().split("면");
//                    splitStrings[0] = splitStrings[0] + "면";
                        splitStrings[1] = splitStrings[1];
                        byRegionCode =
                            guavaRegionRepository.findBySidoAndSigunguAndDongNameAndRiNameAndIsActive(openApiRentBuilding.getSigunguCode()
                                                                                                                         .substring(0,
                                                                                                                                    2),
                                                                                                      openApiRentBuilding.getSigunguCode()
                                                                                                                         .substring(2,
                                                                                                                                    5),
                                                                                                      splitStrings[0] + "면",
                                                                                                      splitStrings[1],
                                                                                                      true);
                        if (!byRegionCode.isPresent()) {
                            byRegionCode =
                                guavaRegionRepository.findBySidoAndSigunguAndDongNameAndRiName(openApiRentBuilding.getSigunguCode()
                                                                                                                  .substring(0,
                                                                                                                             2),
                                                                                               openApiRentBuilding.getSigunguCode()
                                                                                                                  .substring(2,
                                                                                                                             5),
                                                                                               splitStrings[0] + "면",
                                                                                               splitStrings[1]);
                        }
                        if (!byRegionCode.isPresent()) {
                            byRegionCode =
                                guavaRegionRepository.findBySidoAndSigunguAndDongNameAndRiName(openApiRentBuilding.getSigunguCode()
                                                                                                                  .substring(0,
                                                                                                                             2),
                                                                                               openApiRentBuilding.getSigunguCode()
                                                                                                                  .substring(2,
                                                                                                                             5),
                                                                                               splitStrings[0] + "읍",
                                                                                               splitStrings[1]);
                        }
                        if (!byRegionCode.isPresent()) {
                            byRegionCode =
                                guavaRegionRepository.findBySidoAndSigunguAndSigunguNameAndDongName(openApiRentBuilding.getSigunguCode()
                                                                                                                       .substring(0,
                                                                                                                                  2),
                                                                                                    openApiRentBuilding.getSigunguCode()
                                                                                                                       .substring(2,
                                                                                                                                  5),
                                                                                                    splitStrings[0] + "면",
                                                                                                    splitStrings[1]);
                        }
                        if (!byRegionCode.isPresent()) {
                            byRegionCode =
                                guavaRegionRepository.findBySidoAndSigunguAndSigunguNameAndDongName(openApiRentBuilding.getSigunguCode()
                                                                                                                       .substring(0,
                                                                                                                                  2),
                                                                                                    openApiRentBuilding.getSigunguCode()
                                                                                                                       .substring(2,
                                                                                                                                  5),
                                                                                                    splitStrings[0] + "읍",
                                                                                                    splitStrings[1]);
                        }
                        if (!byRegionCode.isPresent()) {
                            byRegionCode = guavaRegionRepository.findBySidoAndSigunguAndNameContains(openApiRentBuilding.getSigunguCode()
                                                                                                                        .substring(0, 2),
                                                                                                     openApiRentBuilding.getSigunguCode()
                                                                                                                        .substring(2, 5),
                                                                                                     splitStrings[0] + "면 " + splitStrings[1]);
                        }
                        if (!byRegionCode.isPresent()) {
                            byRegionCode = guavaRegionRepository.findBySidoAndSigunguAndNameContains(openApiRentBuilding.getSigunguCode()
                                                                                                                        .substring(0, 2),
                                                                                                     openApiRentBuilding.getSigunguCode()
                                                                                                                        .substring(2, 5),
                                                                                                     splitStrings[0] + "읍 " + splitStrings[1]);
                        }
                    }
                }
                if (!byRegionCode.isPresent()) {
                    byRegionCode = guavaRegionRepository.findBySidoAndSigunguAndSigunguNameAndIsActive(openApiRentBuilding.getSigunguCode()
                                                                                                                          .substring(0, 2),
                                                                                                       openApiRentBuilding.getSigunguCode()
                                                                                                                          .substring(2, 5),
                                                                                                       openApiRentBuilding.getDong(),
                                                                                                       true);
                    if (!byRegionCode.isPresent()) {
                        byRegionCode = guavaRegionRepository.findBySidoAndSigunguAndSigunguName(openApiRentBuilding.getSigunguCode()
                                                                                                                   .substring(0, 2),
                                                                                                openApiRentBuilding.getSigunguCode()
                                                                                                                   .substring(2, 5),
                                                                                                openApiRentBuilding.getDong());
                    }
                }
                if (byRegionCode.isPresent()) {
                    GuavaRegion guavaRegion = byRegionCode.get();
                    openApiRentBuilding.setRegionCode(guavaRegion.getRegionCode());
                    result.add(openApiRentBuilding);
//                    buildingList.add(Building.builder()
//                                             .query(openApiRentBuilding.getSigunguCode() + openApiRentBuilding.getDong() +
// openApiRentBuilding.getLotNumber() + openApiRentBuilding
//                                                 .getAptName())
//                                             .type(BuildingType.APARTMENT)
//                                             .regionCode(guavaRegion.getRegionCode())
//                                             .name(guavaRegion.getName())
//                                             .sido(guavaRegion.getSido())
//                                             .sidoName(guavaRegion.getSidoName())
//                                             .sigungu(guavaRegion.getSigungu())
//                                             .sigunguName(guavaRegion.getSigunguName())
//                                             .dong(guavaRegion.getDong())
//                                             .dongName(guavaRegion.getDongName())
//                                             .ri(guavaRegion.getRi())
//                                             .riName(guavaRegion.getRiName())
//                                             .lat(guavaRegion.getLat())
//                                             .lng(guavaRegion.getLng())
//                                             .point(guavaRegion.getPoint())
//                                             .lotNumber(openApiRentBuilding.getLotNumber())
//                                             .buildingName(openApiRentBuilding.getAptName())
//                                             .build());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        Lists.partition(result, 1000).forEach(x -> {
            openApiRentBuildingRepository.saveAll(x);
            log.info("save : {}", x.size());
        });
    }

    @Override
    public void building() {
        List<KaptInfo> kaptInfoList = kaptInfoRepository.findAll()
                                                        .stream()
                                                        .peek(x -> x.setKaptAddr(x.getKaptAddr().replace(x.getKaptName(), "").trim()))
                                                        .collect(Collectors.toList());
        List<Building> tradeList = openApiTradeBuildingRepository.findByRegionCodeIsNotNull()
                                                                 .stream()
                                                                 .map(x -> Building.builder()
                                                                                   .regionCode(x.getRegionCode())
                                                                                   .lotNumber(x.getLotNumber())
                                                                                   .buildingName(x.getAptName())
                                                                                   .build())
                                                                 .collect(Collectors.toList());
        List<Building> rentList = openApiRentBuildingRepository.findByRegionCodeIsNotNull()
                                                               .stream()
                                                               .map(x -> Building.builder()
                                                                                 .regionCode(x.getRegionCode())
                                                                                 .lotNumber(x.getLotNumber())
                                                                                 .buildingName(x.getAptName())
                                                                                 .build())
                                                               .collect(Collectors.toList());
        List<Building> buildingList = Stream.concat(tradeList.stream(), rentList.stream())
                                            .filter(distinctByKeys(x -> x.getRegionCode() + x.getLotNumber() + x.getBuildingName()))
                                            .collect(Collectors.toList());
        List<Building> result = Lists.newArrayList();
        for (Building building : buildingList) {
            try {

                List<String> kaptCodes = kaptInfoList.stream()
                                                     .filter(x -> building.getRegionCode()
                                                                          .equals(x.getBjdCode()) && building.getLotNumber()
                                                                                                             .equals(x.getKaptAddr()))
                                                     .map(KaptInfo::getKaptCode)
                                                     .collect(Collectors.toList());
                log.info("id : {}, codes : {}", building.getBuildingName(), StringUtils.join(kaptCodes, ","));
                building.setKaptCode(StringUtils.join(kaptCodes, ","));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            result.add(building);
        }
        log.info("result : {} / {}", result.size(), buildingList.size());
        Lists.partition(result, 1000).forEach(x -> {
            buildingRepository.saveAll(x);
        });
        log.info("complete : {} / {}", result.size(), buildingList.size());
    }

    @Override
    public void match() {
        List<KaptInfo> kaptInfoList = kaptInfoRepository.findAll()
                                                        .stream()
                                                        .peek(x -> x.setKaptAddr(x.getKaptAddr().replace(x.getKaptName(), "").trim()))
                                                        .collect(Collectors.toList());
        List<Building> buildingList = buildingRepository.findByKaptCodeIsNull();

        List<Building> result = Lists.newArrayList();
        for (Building building : buildingList) {
            try {
                Optional<GuavaRegion> byRegionCode = guavaRegionRepository.findByRegionCode(building.getRegionCode());
                if (byRegionCode.isPresent()) {
                    GuavaRegion guavaRegion = byRegionCode.get();

                    List<String> kaptCodes = kaptInfoList.stream()
                                                         .filter(x -> (guavaRegion.getName() + " " + building.getLotNumber())
                                                             .equals(x.getKaptAddr()))
                                                         .map(KaptInfo::getKaptCode)
                                                         .collect(Collectors.toList());
                    log.info("id : {}, codes : {}", building.getBuildingName(), StringUtils.join(kaptCodes, ","));
                    if (kaptCodes.size() > 0) {
                        building.setKaptCode(StringUtils.join(kaptCodes, ","));
                        result.add(building);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        log.info("result : {} / {}", result.size(), buildingList.size());
        Lists.partition(result, 1000).forEach(x -> {
            buildingRepository.saveAll(x);
        });
        log.info("complete : {} / {}", result.size(), buildingList.size());
    }

    @Override
    public void hgnnMatch() {
        List<Building> all = buildingRepository.findAll();
        List<BuildingMapping> result = Lists.newArrayList();
        for (Building building : all) {
            List<BuildingMapping> guavaMappingInfoList = guavaMappingInfoRepository.findByRegionCodeAndLotNumberAndName(building
                                                                                                                            .getRegionCode(),
                                                                                                                        building
                                                                                                                            .getLotNumber(),
                                                                                                                        building
                                                                                                                            .getBuildingName())
                                                                                   .stream()
                                                                                   .map(x -> BuildingMapping.builder()
                                                                                                            .type(x.getType())
                                                                                                            .regionCode(building.getRegionCode())
                                                                                                            .lotNumber(building.getLotNumber())
                                                                                                            .buildingName(building.getBuildingName())
                                                                                                            .kaptCode(building.getKaptCode())
                                                                                                            .portalId(x.getPortalId())
                                                                                                            .buildingCode(x.getBuildingCode())
                                                                                                            .build())
                                                                                   .collect(Collectors.toList());
            log.info("id : {}, size : {}", building.getId(), guavaMappingInfoList.size());
            if (guavaMappingInfoList.size() > 0) {
                result = Stream.concat(result.stream(), guavaMappingInfoList.stream()).collect(Collectors.toList());
            } else {
                result = Stream.concat(result.stream(), Lists.newArrayList(BuildingMapping.builder()
                                                                                          .regionCode(building.getRegionCode())
                                                                                          .lotNumber(building.getLotNumber())
                                                                                          .buildingName(building.getBuildingName())
                                                                                          .kaptCode(building.getKaptCode())
                                                                                          .build()).stream()).collect(Collectors.toList());
            }
        }
        Lists.partition(result, 1000).forEach(x -> {
            buildingMappingRepository.saveAll(x);
        });
    }

    private TradeItem transform(String hcpcNo, NaverLandResponse.Result.Item item) {
        TradeItem tradeItem = new TradeItem();
        tradeItem.setHcpcNo(hcpcNo);
        tradeItem.setType("NAVER");
        tradeItem.setStartDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        tradeItem.setAtclFetrDesc(item.getAtclFetrDesc());
        tradeItem.setAtclNm(item.getAtclNm());
        tradeItem.setAtclNo(item.getAtclNo());
        tradeItem.setAtclStatCd(item.getAtclStatCd());
        tradeItem.setBildNm(item.getBildNm());
        tradeItem.setCfmYmd(item.getCfmYmd());
        tradeItem.setCpCnt(item.getCpCnt());
        tradeItem.setCpNm(item.getCpNm());
        tradeItem.setCpid(item.getCpid());
        tradeItem.setDirectTradYn(item.getDirectTradYn());
        tradeItem.setDirection(item.getDirection());
        tradeItem.setDtlAddr(item.getDtlAddr());
        tradeItem.setDtlAddrYn(item.getDtlAddrYn());
        tradeItem.setFlrInfo(item.getFlrInfo());
        tradeItem.setPrcInfo(item.getPrcInfo());
        tradeItem.setRepImgThumb(item.getRepImgThumb());
        tradeItem.setRepImgTpCd(item.getRepImgTpCd());
        tradeItem.setRepImgUrl(item.getRepImgUrl());
        tradeItem.setRletTpCd(item.getRletTpCd());
        tradeItem.setRletTpNm(item.getRletTpNm());
        tradeItem.setRltrNm(item.getRltrNm());
        tradeItem.setSameAddrCnt(item.getSameAddrCnt());
        tradeItem.setSameAddrDirectCnt(item.getSameAddrDirectCnt());
        tradeItem.setSameAddrHash(item.getSameAddrHash());
        tradeItem.setSameAddrMaxPrc(item.getSameAddrMaxPrc());
        tradeItem.setSameAddrMinPrc(item.getSameAddrMinPrc());
        tradeItem.setSpc1(item.getSpc1());
        tradeItem.setSpc2(item.getSpc2());
        tradeItem.setTagList(StringUtils.join(item.getTagList(), ","));
        tradeItem.setTradCmplYn(item.getTradCmplYn());
        tradeItem.setTradTpCd(item.getTradTpCd());
        tradeItem.setTradTpNm(item.getTradTpNm());
        tradeItem.setTradeCheckedByOwner(item.getTradeCheckedByOwner());
        tradeItem.setTradePriceHan(item.getTradePriceHan());
        tradeItem.setTradePriceInfo(item.getTradePriceInfo());
        tradeItem.setVrfcTpCd(item.getVrfcTpCd());
        tradeItem.setSellrNm(item.getSellrNm());
        if (item.getCpLinkVO() != null) {
            NaverLandResponse.Result.Item.CpLinkVO cpLinkVO = item.getCpLinkVO();
            tradeItem.setMobileArticleUrl(cpLinkVO.getMobileArticleUrl());
            tradeItem.setMobileArticleLinkTypeCode(cpLinkVO.getMobileArticleLinkTypeCode());
            tradeItem.setMobileArticleLinkUseAtArticleTitle(cpLinkVO.getMobileArticleLinkUseAtArticleTitle());
            tradeItem.setMobileArticleLinkUseAtCpName(cpLinkVO.getMobileArticleLinkUseAtCpName());
            tradeItem.setMobileBmsInspectPassYn(cpLinkVO.getMobileBmsInspectPassYn());
            tradeItem.setPcArticleLinkUseAtArticleTitle(cpLinkVO.getPcArticleLinkUseAtArticleTitle());
            tradeItem.setPcArticleLinkUseAtCpName(cpLinkVO.getPcArticleLinkUseAtCpName());
        }
        return tradeItem;
    }

    public GuavaRegionStats transform(GuavaRegion guavaRegion) {
        log.info("code : {}", guavaRegion.getRegionCode());
        int price = 0;
        int marketPrice = 0;

        String dongCode = guavaRegion.getRegionCode().substring(5, 10);
        String sigunguCode = guavaRegion.getRegionCode().substring(0, 5);
        List<OpenApiTradeInfo> openApiTradeInfoList = openApiTradeInfoRepository.getTrade(sigunguCode, dongCode,
                                                                                          String.valueOf(YearMonth.now().getYear()));
        if (openApiTradeInfoList.size() == 0) {
            return null;
        }
        price = openApiTradeInfoList.stream()
                                    .map(x -> Integer.valueOf(x.getPrice().replace(",", "")) / x.getArea())
                                    .reduce((a, b) -> a + b).orElse(0d).intValue();
        if (price != 0) {
            price = (int) (Math.floor((price / openApiTradeInfoList.size()) * 84 / 1000) * 1000);
        }
        List<GuavaMappingInfo> byRegionCode = guavaMappingInfoRepository.findByRegionCode(guavaRegion.getRegionCode());
        List<TradeItem> collect = byRegionCode.stream()
                                              .map(GuavaMappingInfo::getPortalId)
                                              .map(tradeItemRepository::findByHcpcNo)
                                              .flatMap(Collection::stream).collect(Collectors.toList());
        marketPrice = collect.stream()
                             .map(x -> x.getPrice() / Double.valueOf(x.getSpc2()))
                             .reduce((a, b) -> a + b).orElse(0d).intValue();
        if (marketPrice != 0) {
            marketPrice = (int) (Math.floor((marketPrice / collect.size()) * 84 / 1000) * 1000);
        }
        return GuavaRegionStats.builder()
                               .price(price)
                               .marketPrice(marketPrice)
                               .regionCode(guavaRegion.getRegionCode())
                               .build();
    }

    //    @Async("executorSample")
    private GuavaHgnnBuilding executor(String query) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start(query);
            GuavaHgnnBuilding data = hgnnLambdaClient.drawTest(HgnnLambdaRequest.builder()
                                                                                .query(query)
                                                                                .build()).getResult().getData();
            stopWatch.stop();
//            log.info("time : {} | query : {}", stopWatch.getTotalTimeSeconds(), query);
            return data;
        } catch (Exception e) {
            log.error("query : {}, message : {}", e.getMessage());
            GuavaBuildingFailInfo guavaBuildingFailInfo = new GuavaBuildingFailInfo();
            guavaBuildingFailInfo.setBuildingCode(query);
//            guavaBuildingFailInfoRepository.save(guavaBuildingFailInfo);
        }
        return null;
    }

    private GuavaBuilding transform(GuavaHgnnBuilding guavaHgnnBuilding) {
        GuavaHgnnBaseInfo baseinfo = guavaHgnnBuilding.getBaseinfo();
        GuavaBuilding build = GuavaBuilding.builder()
                                           .buildingCode(guavaHgnnBuilding.getId())
                                           .type(guavaHgnnBuilding.getType())
                                           .adminRegionCode(guavaHgnnBuilding.getAdminRegionCode())
                                           .regionCode(guavaHgnnBuilding.getRegionCode())
                                           .originalName("")
                                           .name(guavaHgnnBuilding.getName())
                                           .address(guavaHgnnBuilding.getAddress())
                                           .roadAddress(guavaHgnnBuilding.getRoadAddress())
                                           .portalId(guavaHgnnBuilding.getPortalId())
                                           .totalHousehold(guavaHgnnBuilding.getTotalHousehold())

//                                               .manageCostYear(baseinfo.ifPresent(x -> x.ge_))
//                                               .manageCostSummer()
//                                               .manageCostWinter()
                                           .floorAreaRatio(baseinfo != null && baseinfo.getFloorAreaRatio() != null ?
                                                               baseinfo.getFloorAreaRatio() : 0)
                                           .buildingCoverageRatio(baseinfo != null && baseinfo.getBuildingCoverageRatio() != null ? baseinfo
                                               .getBuildingCoverageRatio() : 0)
                                           .buildingCount(baseinfo != null && baseinfo.getBuildingCount() != null ?
                                                              baseinfo.getBuildingCount() : 0)
                                           .floorMax(baseinfo != null && baseinfo.getFloorMax() != null ? baseinfo.getFloorMax()
                                                         : 0)
                                           .floorMin(baseinfo != null && baseinfo.getFloorMin() != null ? baseinfo.getFloorMin()
                                                         : 0)
                                           .tel(baseinfo != null && baseinfo.getTel() != null ? baseinfo.getTel() : "")
                                           .parkingTotal(baseinfo != null && baseinfo.getParkingTotal() != null ?
                                                             baseinfo.getParkingTotal() : 0)
                                           .parkingInside(baseinfo != null && baseinfo.getParkingInside() != null ?
                                                              baseinfo.getParkingInside() : 0)
                                           .parkingOutside(baseinfo != null && baseinfo.getParkingOutside() != null ?
                                                               baseinfo.getParkingOutside() : 0)
                                           .parkingRate(baseinfo != null && baseinfo.getParkingRate() != null ?
                                                            baseinfo.getParkingRate() : 0)
                                           .approvalDate(baseinfo != null && baseinfo.getApprovalDate() != null ?
                                                             baseinfo.getApprovalDate() : "")
                                           .baseInfoId(baseinfo != null && baseinfo.getId() != null ? baseinfo.getId() : "")
                                           .totalHousehold(baseinfo != null && baseinfo.getTotalHousehold() != null ?
                                                               baseinfo.getTotalHousehold() : 0)
                                           .totalHouseholdCounted(baseinfo != null && baseinfo.getTotalHouseholdCounted() != null ? baseinfo
                                               .getTotalHouseholdCounted() : 0)
                                           .company(baseinfo != null && baseinfo.getCompany() != null ? baseinfo.getCompany() : "")
                                           .asileType(baseinfo != null && baseinfo.getAsileType() != null ?
                                                          baseinfo.getAsileType() : "")
                                           .heatType(baseinfo != null && baseinfo.getHeatType() != null ? baseinfo.getHeatType()
                                                         : "")
                                           .heatSource(baseinfo != null && baseinfo.getHeatSource() != null ?
                                                           baseinfo.getHeatSource() : "")

                                           .rentalBusinessRatio(guavaHgnnBuilding.getRentalBusinessRatio())
                                           .totalRentalBusinessHousehold(guavaHgnnBuilding.getTotalRentalBusinessHousehold())
                                           .businessCallPrice(guavaHgnnBuilding.getBusinessCallPrice())
                                           .startMonth(guavaHgnnBuilding.getStartMonth())
                                           .lat(guavaHgnnBuilding.getLat())
                                           .lng(guavaHgnnBuilding.getLng())
                                           .point(this.createPoint(guavaHgnnBuilding.getLng(), guavaHgnnBuilding.getLat()))
                                           .roadviewLat(guavaHgnnBuilding.getRoadviewLat())
                                           .roadviewLng(guavaHgnnBuilding.getRoadviewLng())
                                           .diffYearText(guavaHgnnBuilding.getDiffYearText())
                                           .diffYearShortText(guavaHgnnBuilding.getDiffYearShortText())
                                           .dong(guavaHgnnBuilding.getDong())
//                                               .moliteId(data.getId())
//                                               .moliteCategory()
                                           .build();
        List<GuavaHgnnBuilding.Area> areaList = guavaHgnnBuilding.getArea();
        if (areaList != null) {
            for (GuavaHgnnBuilding.Area buildingArea : areaList) {
                GuavaBuildingArea guavaBuildingArea = new GuavaBuildingArea();
                guavaBuildingArea.setBuildingCode(build.getBuildingCode());
                guavaBuildingArea.setPrivateArea(buildingArea.getPrivateArea());
                guavaBuildingArea.setPublicArea(buildingArea.getPublicArea());
                guavaBuildingArea.setTotalHousehold(buildingArea.getTotalHousehold());
                guavaBuildingArea.setAreaType(buildingArea.getAreaType());
                build.addArea(guavaBuildingArea);
            }
        }
//        log.info("build : {}", build.getBuildingCode());
        return build;
    }

    private Point createPoint(double lng, double lat) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPoint(new Coordinate(lng, lat));
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
