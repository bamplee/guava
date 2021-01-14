package net.moboo.batch.application.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.common.utils.SyncUtils;
import net.moboo.batch.domain.AptTradeDetail;
import net.moboo.batch.domain.OpenApiTradeInfo;
import net.moboo.batch.domain.RegionType;
import net.moboo.batch.domain.TradeItem;
import net.moboo.batch.hgnn.repository.GuavaBuilding;
import net.moboo.batch.hgnn.repository.GuavaBuildingRepository;
import net.moboo.batch.hgnn.repository.GuavaMappingInfo;
import net.moboo.batch.hgnn.repository.GuavaMappingInfoRepository;
import net.moboo.batch.hgnn.repository.GuavaRegion;
import net.moboo.batch.hgnn.repository.GuavaRegionRepository;
import net.moboo.batch.infrastructure.feign.AptTradeApiClient;
import net.moboo.batch.infrastructure.jpa.GuavaRegionStats;
import net.moboo.batch.infrastructure.jpa.GuavaRegionStatsRepository;
import net.moboo.batch.infrastructure.jpa.OpenApiTradeInfoRepository;
import net.moboo.batch.infrastructure.jpa.TradeItemRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
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

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class TradeSyncServiceImpl implements TradeSyncService {
    private final AptTradeApiClient aptTradeApiClient;
    private final OpenApiTradeInfoRepository openApiTradeInfoRepository;
    private final GuavaBuildingRepository guavaBuildingRepository;
    private final GuavaRegionRepository guavaRegionRepository;
    private final GuavaMappingInfoRepository guavaMappingInfoRepository;
    private final TradeItemRepository tradeItemRepository;
    private final GuavaRegionStatsRepository guavaRegionStatsRepository;
    @Value("${rent-list-api.api.key}")
    private String serviceKey;
    private String numOfRows = "1000000";
    private String pageNo = "1";
//    private RegionStatsService regionStatsService;

    public TradeSyncServiceImpl(AptTradeApiClient aptTradeApiClient,
                                OpenApiTradeInfoRepository openApiTradeInfoRepository,
                                GuavaBuildingRepository guavaBuildingRepository,
                                GuavaRegionRepository guavaRegionRepository,
                                GuavaMappingInfoRepository guavaMappingInfoRepository,
                                TradeItemRepository tradeItemRepository,
                                GuavaRegionStatsRepository guavaRegionStatsRepository) {
        this.aptTradeApiClient = aptTradeApiClient;
        this.openApiTradeInfoRepository = openApiTradeInfoRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaMappingInfoRepository = guavaMappingInfoRepository;
        this.tradeItemRepository = tradeItemRepository;
        this.guavaRegionStatsRepository = guavaRegionStatsRepository;
    }

    @Override
    public List<OpenApiTradeInfo> getOpenApiTradeInfo(YearMonth yearMonth) {
        log.info(yearMonth.toString());
        // 1. A list - 기존에 가지고 있는 YYYMM에 해당하는 실거래 정보를 가져옴
        List<OpenApiTradeInfo> beforeList = openApiTradeInfoRepository.findByYearAndMonth(String.valueOf(yearMonth.getYear()),
                                                                                          String.valueOf(yearMonth.getMonth()
                                                                                                                  .getValue()));
        // 2. B list - openapi에서 YYYYMM 및 지역에 해당하는 아파트 정보를 가져와서 하나의 리스트로 생성
        List<OpenApiTradeInfo> afterList = Lists.newArrayList();
        List<GuavaRegion> regionList = guavaRegionRepository.findByRegionCodeEndsWith("00000");
        for (GuavaRegion region : regionList) {
            String regionCode = region.getSido() + region.getSigungu();
            Stream<OpenApiTradeInfo> apiResultList = getOpenApiTradeInfo(yearMonth, regionCode).stream();
            afterList = Stream.concat(afterList.stream(), apiResultList)
                              .collect(Collectors.toList());
        }
        // 3. A리스트의 아이템들과 B리스트의 아이템을 비교하여 중복되는것을 B리스트에서 제거함
        List<OpenApiTradeInfo> uniqueList = Lists.newArrayList(afterList);
        beforeList.forEach(beforeItem -> uniqueList.stream()
                                                   .filter(afterItem -> this.isDuplicated(beforeItem, afterItem))
                                                   .findFirst()
                                                   .ifPresent(uniqueList::remove));
//        for (OpenApiTradeInfo openApiTradeInfo : uniqueList) {
//            String regionCode = openApiTradeInfo.getDongSigunguCode() + openApiTradeInfo.getDongCode();
//            Optional<GuavaRegion> first = guavaRegionRepository.findByRegionCode(regionCode);
//
//            if (first.isPresent()) {
//                GuavaRegion guavaRegion = first.get();
//                openApiTradeInfo.setRegionId(guavaRegion.getRegionCode());
//                Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findByAddressContaining(guavaRegion.getName() +
// " " + openApiTradeInfo.getLotNumber()).stream().findFirst();
//                if (optionalGuavaBuilding.isPresent()) {
//                    GuavaBuilding guavaBuilding = optionalGuavaBuilding.get();
//                    openApiTradeInfo.setBuildingId(guavaBuilding.getBuildingCode());
//                }
//            }
//        }
        log.info("beforeList size : {}, afterList size : {}, uniqueList size : {}", beforeList.size(), afterList.size(), uniqueList.size());
        return uniqueList;
    }

    @Override
    public List<OpenApiTradeInfo> setOpenApiTradeInfo(List<OpenApiTradeInfo> uniqueList) {
        return openApiTradeInfoRepository.saveAll(uniqueList);
    }

    @Override
    public OpenApiTradeInfo process(OpenApiTradeInfo openApiTradeInfo) {
//        String month = openApiTradeInfo.getMonth().length() == 1 ? String.format("0%s",
//                                                                                 openApiTradeInfo.getMonth()) : openApiTradeInfo.getMonth();
//        String day = openApiTradeInfo.getDay().length() == 1 ? String.format("0%s", openApiTradeInfo.getDay()) : openApiTradeInfo.getDay();
//        String date = openApiTradeInfo.getYear() + month + day;
//        String regionCode = openApiTradeInfo.getDongSigunguCode() + openApiTradeInfo.getDongCode();
//        openApiTradeInfo.setDate(date);
//        openApiTradeInfo.setRegionCode(regionCode);
//        openApiTradeInfo.setPrice(openApiTradeInfo.getPrice().replaceAll(" ", "").replaceAll(",", ""));
//
//        Optional<GuavaRegion> first = guavaRegionRepository.findByRegionCode(regionCode);
//
//        if (first.isPresent()) {
//            GuavaRegion guavaRegion = first.get();
//            String address = guavaRegion.getName() + " " + openApiTradeInfo.getLotNumber();
//            Optional<GuavaBuilding> optionalGuavaBuilding = guavaBuildingRepository.findByAddressContaining(address).stream().findFirst();
//            if (optionalGuavaBuilding.isPresent()) {
//                GuavaBuilding guavaBuilding = optionalGuavaBuilding.get();
//                openApiTradeInfo.setBuildingId(guavaBuilding.getBuildingCode());
//            }
//        }
        return openApiTradeInfo;
    }

    @Override
    public List<GuavaRegionStats> tradeStatsRead() {
        return guavaRegionRepository.findAll()
                                    .parallelStream()
                                    .map(this::transform)
                                    .filter(ObjectUtils::isNotEmpty)
                                    .collect(Collectors.toList());
    }

    public GuavaRegionStats transform(GuavaRegion guavaRegion) {
        log.info("code : {}", guavaRegion.getRegionCode());
        int price = 0;
        int marketPrice = 0;

        String dongCode = guavaRegion.getRegionCode().substring(5, 10);
        String sigunguCode = guavaRegion.getRegionCode().substring(0, 5);
        List<OpenApiTradeInfo> tradeList = openApiTradeInfoRepository.getTrade("2020", sigunguCode, dongCode);
        if (tradeList.size() != 0) {
            price = tradeList.stream()
                             .map(x -> Integer.valueOf(x.getPrice()
                                                        .replace(",",
                                                                 "")) / Double.valueOf(x.getArea()))
                             .reduce((a, b) -> a + b).orElse(0d).intValue();
            if (price != 0) {
                price = (int) (Math.floor((price / tradeList.size()) * 84 / 1000) * 1000);
            }
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

    @Override
    public void tradeStatsWrite(List<GuavaRegionStats> tradeItems) {
        log.info("size : {} ", tradeItems.size());
        guavaRegionStatsRepository.saveAll(tradeItems);
    }

    private OpenApiTradeInfo transformOpenApiTradeInfo(AptTradeDetail.Body.Items.Item item) {
        OpenApiTradeInfo openApiTradeInfo = new OpenApiTradeInfo();
        openApiTradeInfo.setPrice(SyncUtils.replaceEmptyStr(item.getPrice()));
        openApiTradeInfo.setSince(SyncUtils.replaceEmptyStr(item.getSince()));
        openApiTradeInfo.setYear(SyncUtils.replaceEmptyStr(item.getYear()));
        openApiTradeInfo.setRoad(SyncUtils.replaceEmptyStr(item.getRoad()));
        openApiTradeInfo.setRoadMainCode(SyncUtils.replaceEmptyStr(item.getRoadMainCode()));
        openApiTradeInfo.setRoadSubCode(SyncUtils.replaceEmptyStr(item.getRoadSubCode()));
        openApiTradeInfo.setRoadSigunguCode(SyncUtils.replaceEmptyStr(item.getRoadSigunguCode()));
        openApiTradeInfo.setRoadSerialNumberCode(SyncUtils.replaceEmptyStr(item.getRoadSerialNumberCode()));
        openApiTradeInfo.setRoadGroundCode(SyncUtils.replaceEmptyStr(item.getRoadGroundCode()));
        openApiTradeInfo.setRoadCode(SyncUtils.replaceEmptyStr(item.getRoadCode()));
        openApiTradeInfo.setDong(SyncUtils.replaceEmptyStr(item.getDong()));
        openApiTradeInfo.setDongCode(SyncUtils.replaceEmptyStr(item.getDongCode()));
        openApiTradeInfo.setDongMainCode(SyncUtils.replaceEmptyStr(item.getDongMainCode()));
        openApiTradeInfo.setDongSubCode(SyncUtils.replaceEmptyStr(item.getDongSubCode()));
        openApiTradeInfo.setDongSigunguCode(SyncUtils.replaceEmptyStr(item.getDongSigunguCode()));
        openApiTradeInfo.setDongLotNumberCode(SyncUtils.replaceEmptyStr(item.getDongLotNumberCode()));
        openApiTradeInfo.setAptName(SyncUtils.replaceEmptyStr(item.getAptName()));
        openApiTradeInfo.setMonth(SyncUtils.replaceEmptyStr(item.getMonth()));
        openApiTradeInfo.setDay(SyncUtils.replaceEmptyStr(item.getDay()));
        openApiTradeInfo.setSerialNumber(SyncUtils.replaceEmptyStr(item.getSerialNumber()));
        openApiTradeInfo.setArea(SyncUtils.replaceEmptyStr(item.getArea()));
        openApiTradeInfo.setLotNumber(SyncUtils.replaceEmptyStr(item.getLotNumber()));
        openApiTradeInfo.setRegionCode(SyncUtils.replaceEmptyStr(item.getRegionCode()));
        openApiTradeInfo.setFloor(SyncUtils.replaceEmptyStr(item.getFloor()));
        return openApiTradeInfo;
    }

    @Async("executorSample")
    public List<OpenApiTradeInfo> getOpenApiTradeInfo(YearMonth yearMonth, String gunguCode) {
        AptTradeDetail aptTradeDetailList;
        try {
            aptTradeDetailList = aptTradeApiClient.getAptTradeDetailList(serviceKey,
                                                                         SyncUtils.getYyyyMmDate(yearMonth),
                                                                         gunguCode,
                                                                         numOfRows,
                                                                         pageNo);
        } catch (feign.FeignException fe) {
            log.info(fe.getMessage());
            aptTradeDetailList = aptTradeApiClient.getAptTradeDetailList(serviceKey,
                                                                         SyncUtils.getYyyyMmDate(yearMonth),
                                                                         gunguCode,
                                                                         numOfRows,
                                                                         pageNo);
        }
        return aptTradeDetailList.getBody()
                                 .getItems()
                                 .getItem()
                                 .stream()
                                 .map(this::transformOpenApiTradeInfo)
                                 .collect(Collectors.toList());
    }

    private Boolean isDuplicated(OpenApiTradeInfo beforeItem, OpenApiTradeInfo afterItem) {
        return beforeItem.getSerialNumber()
                         .equals(afterItem.getSerialNumber())
            && beforeItem.getAptName()
                         .equals(afterItem.getAptName())
            && beforeItem.getArea()
                         .equals(afterItem.getArea())
            && beforeItem.getFloor()
                         .equals(afterItem.getFloor())
            && beforeItem.getPrice()
                         .equals(afterItem.getPrice())
            && beforeItem.getYear()
                         .equals(afterItem.getYear())
            && beforeItem.getMonth()
                         .equals(afterItem.getMonth())
            && beforeItem.getDay()
                         .equals(afterItem.getDay())
            && beforeItem.getDongCode()
                         .equals(afterItem.getDongCode());
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
