package net.moboo.batch.application.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.domain.OpenApiRentInfo;
import net.moboo.batch.domain.OpenApiTradeInfo;
import net.moboo.batch.domain.TradeType;
import net.moboo.batch.hgnn.repository.GuavaBuildingArea;
import net.moboo.batch.hgnn.repository.GuavaBuildingAreaRepository;
import net.moboo.batch.infrastructure.jpa.OpenApiRentInfoRepository;
import net.moboo.batch.wooa.repository.BuildingMapping;
import net.moboo.batch.wooa.repository.BuildingMappingRepository;
import net.moboo.batch.wooa.repository.RentSummary;
import net.moboo.batch.wooa.repository.RentSummaryRepository;
import net.moboo.batch.wooa.repository.TradeSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RentSummaryServiceImpl implements RentSummaryService {
    private final OpenApiRentInfoRepository openApiRentInfoRepository;
    private final GuavaBuildingAreaRepository guavaBuildingAreaRepository;
    private final BuildingMappingRepository buildingMappingRepository;
    private final RentSummaryRepository rentSummaryRepository;

    public RentSummaryServiceImpl(OpenApiRentInfoRepository openApiRentInfoRepository,
                                  GuavaBuildingAreaRepository guavaBuildingAreaRepository,
                                  BuildingMappingRepository buildingMappingRepository,
                                  RentSummaryRepository rentSummaryRepository) {
        this.openApiRentInfoRepository = openApiRentInfoRepository;
        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
        this.buildingMappingRepository = buildingMappingRepository;
        this.rentSummaryRepository = rentSummaryRepository;
    }

    @Override
    public List<RentSummary> read() {
        return null;
    }

    @Override
    public List<RentSummary> write(List<List<RentSummary>> tradeSummaries) {
        return rentSummaryRepository.saveAll(tradeSummaries.stream().flatMap(Collection::stream).collect(Collectors.toList()));
    }

    @Override
    public List<RentSummary> process(BuildingMapping buildingMapping) {
        if (buildingMapping.getBuildingCode() == null) {
            return Lists.newArrayList();
        }
        if (buildingMapping.getType() != null && buildingMapping.getType() != 0) {
            return Lists.newArrayList();
        }
        String regionCode = buildingMapping.getRegionCode();
        String sigunguCode = regionCode.substring(0, 5);
        String dongCode = regionCode.substring(5);
        List<OpenApiRentInfo> openApiRentInfos = openApiRentInfoRepository.findByRegionCodeAndLotNumberAndAptName(
            sigunguCode,
            buildingMapping.getLotNumber(),
            buildingMapping.getBuildingName());
//        List<TradeSummary> byBuildingCode = tradeSummaryRepository.findByBuildingCode(buildingMapping.getBuildingCode());
        List<GuavaBuildingArea> buildingAreas = guavaBuildingAreaRepository.findByBuildingCode(buildingMapping.getBuildingCode());

        List<RentSummary> collect = openApiRentInfos.stream().map(x -> {
            String month = x.getMonth().length() == 1 ? String.format("0%s", x.getMonth()) : x.getMonth();
            String day = x.getDay().length() == 1 ? String.format("0%s", x.getDay()) : x.getDay();
            String date = x.getYear() + month + day;

            String price = Optional.ofNullable(x.getPrice()).orElse("").replaceAll("[^0-9]", "");
            String subPrice = Optional.ofNullable(x.getMonthlyRent()).orElse("").replaceAll("[^0-9]", "");
            String floor = Optional.ofNullable(x.getFloor()).orElse("").replaceAll("[^0-9]", "");

            RentSummary.RentSummaryBuilder tradeSummaryBuilder = RentSummary.builder()
                                                                            .id(x.getId())
                                                                            .name(x.getAptName())
                                                                            .price(NumberUtils.toInt(price, -1))
                                                                            .subPrice(NumberUtils.toInt(subPrice, -1))
                                                                            .floor(NumberUtils.toInt(floor, -1))
                                                                            .type(NumberUtils.toInt(subPrice,
                                                                                                    -1) > 0 ? TradeType.MONTHLY_RENT :
                                                                                      TradeType.RENT)
                                                                            .date(date)
                                                                            .regionCode(buildingMapping.getRegionCode())
                                                                            .buildingCode(buildingMapping.getBuildingCode());
            if (buildingAreas.size() > 0 && StringUtils.isNotEmpty(x.getArea())) {
                // 4ba
                Optional<GuavaBuildingArea> optionalGuavaBuildingArea = buildingAreas.stream()
                                                                                     .filter(y -> String.valueOf(y.getPrivateArea())
                                                                                                        .equals(x.getArea()))
                                                                                     .findFirst();
                // public area 로 비교
                if (!optionalGuavaBuildingArea.isPresent()) {
                    optionalGuavaBuildingArea = buildingAreas.stream()
                                                             .filter(y -> String.valueOf(y.getPublicArea())
                                                                                .equals(x.getArea()))
                                                             .findFirst();
                }
                // 가장 유사한 private area 계산
                if (!optionalGuavaBuildingArea.isPresent()) {
                    GuavaBuildingArea areaByPrivateArea = getAreaByPrivateArea(buildingAreas, x.getArea());
                    optionalGuavaBuildingArea = Optional.of(areaByPrivateArea);
                }
                GuavaBuildingArea guavaBuildingArea = optionalGuavaBuildingArea.get();
                tradeSummaryBuilder = tradeSummaryBuilder.areaCode(String.valueOf(guavaBuildingArea.getId()))
                                                         .privateArea(guavaBuildingArea.getPrivateArea())
                                                         .publicArea(guavaBuildingArea.getPublicArea())
                                                         .areaType(guavaBuildingArea.getAreaType());
            }
            return tradeSummaryBuilder.build();
        }).collect(Collectors.toList());
        log.info("building : {}({}), {}/{}",
                 buildingMapping.getBuildingName(),
                 buildingMapping.getBuildingCode(),
                 collect.size(),
                 openApiRentInfos.size());
        return null;
    }

    public static GuavaBuildingArea getAreaByPrivateArea(List<GuavaBuildingArea> guavaBuildingAreaList, String privateArea) {
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
}
