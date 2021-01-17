package im.prize.api.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import im.prize.api.hgnn.repository.BuildingMapping;
import im.prize.api.hgnn.repository.BuildingMappingRepository;
import im.prize.api.hgnn.repository.GuavaBuildingAreaRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.interfaces.GuavaTradeSearch;
import im.prize.api.interfaces.response.GuavaChartResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GuavaTradeChartServiceImpl implements GuavaTradeChartService {
    @Value("${app.kakao.apiKey}")
    private String kakaoMapApiKey;
    private final GuavaRegionRepository guavaRegionRepository;
    private final GuavaBuildingAreaRepository guavaBuildingAreaRepository;
    private final BuildingMappingRepository buildingMappingRepository;
    private final TradeSummaryRepository tradeSummaryRepository;
    private final ObjectMapper objectMapper;

    public static final DateTimeFormatter DATE_TIME_FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public GuavaTradeChartServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                      GuavaBuildingAreaRepository guavaBuildingAreaRepository,
                                      BuildingMappingRepository buildingMappingRepository,
                                      TradeSummaryRepository tradeSummaryRepository, ObjectMapper objectMapper) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
        this.buildingMappingRepository = buildingMappingRepository;
        this.tradeSummaryRepository = tradeSummaryRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<GuavaChartResponse> getRegionChartList(String regionId,
                                                       Integer startArea,
                                                       Integer endArea,
                                                       String startDate,
                                                       String endDate) {
        Optional<GuavaRegion> optionalGuavaRegion = guavaRegionRepository.findById(Long.valueOf(regionId));
        if (optionalGuavaRegion.isPresent()) {
            GuavaRegion guavaRegion = optionalGuavaRegion.get();
            List<TradeSummary> tradeSummaryPage = tradeSummaryRepository.findAll(this.getParams(guavaRegion.getValidRegionCode(),
                                                                                                null,
                                                                                                null,
                                                                                                startDate, endDate));
            return tradeSummaryPage.stream()
                                   .map(GuavaChartResponse::transform)
//                                   // fixme building id값 조회 안하도록
//                                   .peek(x -> x.setBuildingId(buildingId))
                                   .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    @Override
    public List<GuavaChartResponse> getChartList(String buildingCode, String areaId, String startDate, String endDate) {
        Optional<BuildingMapping> optionalBuildingMapping = buildingMappingRepository.findById(Long.valueOf(buildingCode));
        if (optionalBuildingMapping.isPresent()) {
            BuildingMapping buildingMapping = optionalBuildingMapping.get();
            List<TradeSummary> tradeSummaryPage = tradeSummaryRepository.findAll(this.getParams(null,
                                                                                                buildingMapping.getBuildingCode(),
                                                                                                areaId,
                                                                                                startDate, endDate));
            return tradeSummaryPage.stream()
                                   .map(GuavaChartResponse::transform)
//                                   // fixme building id값 조회 안하도록
//                                   .peek(x -> x.setBuildingId(buildingId))
                                   .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private Specification<TradeSummary> getParams(String regionCode,
                                                  String buildingCode,
                                                  String areaCode,
                                                  String startDate,
                                                  String endDate) {
        Map<String, Object> paramsMap = objectMapper.convertValue(GuavaTradeSearch.builder()
                                                                                  .regionCode(regionCode)
                                                                                  .buildingCode(buildingCode)
                                                                                  .areaCode(areaCode)
                                                                                  .startDate(startDate)
                                                                                  .endDate(endDate)
                                                                                  .build(), Map.class);
        Map<TradeSummarySpecs.SearchKey, Object> params = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            if (ObjectUtils.isNotEmpty(entry.getValue()) && StringUtils.isNotEmpty(String.valueOf(entry.getValue()))) {
                params.put(TradeSummarySpecs.SearchKey.convert(entry.getKey()), entry.getValue());
            }
        }
        return TradeSummarySpecs.searchWith(params);
    }
}
