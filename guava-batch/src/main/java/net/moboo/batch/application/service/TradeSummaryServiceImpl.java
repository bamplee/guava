package net.moboo.batch.application.service;

import com.google.common.collect.Lists;
import net.moboo.batch.domain.OpenApiTradeInfo;
import net.moboo.batch.domain.TradeSummary;
import net.moboo.batch.domain.TradeType;
import net.moboo.batch.hgnn.repository.GuavaBuildingArea;
import net.moboo.batch.hgnn.repository.GuavaBuildingAreaRepository;
import net.moboo.batch.infrastructure.jpa.OpenApiTradeInfoRepository;
import net.moboo.batch.wooa.repository.BuildingMapping;
import net.moboo.batch.wooa.repository.BuildingMappingRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TradeSummaryServiceImpl implements TradeSummaryService {
    private final OpenApiTradeInfoRepository openApiTradeInfoRepository;
    private final GuavaBuildingAreaRepository guavaBuildingAreaRepository;
    private final BuildingMappingRepository buildingMappingRepository;

    public TradeSummaryServiceImpl(OpenApiTradeInfoRepository openApiTradeInfoRepository,
                                   GuavaBuildingAreaRepository guavaBuildingAreaRepository,
                                   BuildingMappingRepository buildingMappingRepository) {
        this.openApiTradeInfoRepository =
            openApiTradeInfoRepository;
        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
        this.buildingMappingRepository = buildingMappingRepository;
    }

    @Override
    public List<TradeSummary> read() {
        return null;
    }

    @Override
    public List<TradeSummary> write(List<List<TradeSummary>> tradeSummaries) {
        return null;
    }

    @Override
    public List<TradeSummary> process(BuildingMapping buildingMapping) {
        if(buildingMapping.getBuildingCode() == null) {
            return Lists.newArrayList();
        }
        if(buildingMapping.getType() != null && buildingMapping.getType() != 0) {
            return Lists.newArrayList();
        }
        String regionCode = buildingMapping.getRegionCode();
        String sigunguCode = regionCode.substring(0, 5);
        String dongCode = regionCode.substring(5);
        List<OpenApiTradeInfo> openApiTradeInfos = openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndLotNumberAndAptName(
            sigunguCode,
            dongCode,
            buildingMapping.getLotNumber(),
            buildingMapping.getBuildingName());
        List<GuavaBuildingArea> buildingAreas = guavaBuildingAreaRepository.findByBuildingCode(buildingMapping.getBuildingCode());

        List<TradeSummary> collect = openApiTradeInfos.stream().map(x -> {
            String month = x.getMonth().length() == 1 ? String.format("0%s", x.getMonth()) : x.getMonth();
            String day = x.getDay().length() == 1 ? String.format("0%s", x.getDay()) : x.getDay();
            String date = x.getYear() + month + day;

            TradeSummary.TradeSummaryBuilder tradeSummaryBuilder = TradeSummary.builder()
                                                                               .type(TradeType.TRADE)
                                                                               .name(x.getAptName())
                                                                               .price(Integer.valueOf(x.getPrice()
                                                                                                       .replaceAll("[^0-9]", "")))
                                                                               .floor(Integer.valueOf(x.getFloor()
                                                                                                       .replaceAll("[^0-9]", "")))
                                                                               .date(date)
                                                                               .regionCode(buildingMapping.getRegionCode())
                                                                               .buildingCode(buildingMapping.getBuildingCode());
            if(buildingAreas.size() > 0) {
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
                    GuavaBuildingArea areaByPrivateArea = this.getAreaByPrivateArea(buildingAreas, x.getArea());
7                    optionalGuavaBuildingArea = Optional.of(areaByPrivateArea);
                }
                if (optionalGuavaBuildingArea.isPresent()) {
                    GuavaBuildingArea guavaBuildingArea = optionalGuavaBuildingArea.get();
                    tradeSummaryBuilder = tradeSummaryBuilder.areaCode(String.valueOf(guavaBuildingArea.getId()))
                                                             .privateArea(guavaBuildingArea.getPrivateArea())
                                                             .publicArea(guavaBuildingArea.getPublicArea())
                                                             .areaType(guavaBuildingArea.getAreaType());
                }
            }
            return tradeSummaryBuilder.build();
        }).collect(Collectors.toList());
        return collect;
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
