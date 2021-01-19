package im.prize.api.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import im.prize.api.domain.oboo.OpenApiRentInfo;
import im.prize.api.hgnn.repository.BuildingMapping;
import im.prize.api.hgnn.repository.BuildingMappingRepository;
import im.prize.api.hgnn.repository.GuavaBuildingAreaRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.OpenApiRentInfoRepository;
import im.prize.api.interfaces.GuavaTradeSearch;
import im.prize.api.interfaces.response.GuavaTradeResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GuavaRentServiceImpl implements GuavaRentService {
    private static final Integer PAGE_SIZE = 30;

    @Value("${app.kakao.apiKey}")
    private String kakaoMapApiKey;
    private final GuavaRegionRepository guavaRegionRepository;
    private final GuavaBuildingRepository guavaBuildingRepository;
    private final OpenApiRentInfoRepository openApiRentInfoRepository;
    private final GuavaBuildingAreaRepository guavaBuildingAreaRepository;
    private final BuildingMappingRepository buildingMappingRepository;
    private final RentSummaryRepository rentSummaryRepository;
    private final ObjectMapper objectMapper;

    public static final DateTimeFormatter DATE_TIME_FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public GuavaRentServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                GuavaBuildingRepository guavaBuildingRepository,
                                OpenApiRentInfoRepository openApiRentInfoRepository,
                                GuavaBuildingAreaRepository guavaBuildingAreaRepository,
                                BuildingMappingRepository buildingMappingRepository,
                                RentSummaryRepository rentSummaryRepository,
                                ObjectMapper objectMapper) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.openApiRentInfoRepository = openApiRentInfoRepository;
        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
        this.buildingMappingRepository = buildingMappingRepository;
        this.rentSummaryRepository = rentSummaryRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<GuavaTradeResponse> getRegionTrade(String regionId, Integer page, Integer startArea, Integer endArea, String date) {
        Optional<GuavaRegion> optionalGuavaRegion = guavaRegionRepository.findById(Long.valueOf(regionId));
        if (optionalGuavaRegion.isPresent()) {
            // todo
            GuavaRegion guavaRegion = optionalGuavaRegion.get();
            Page<RentSummary> tradeSummaryPage = rentSummaryRepository.findAll(this.getParams(guavaRegion.getValidRegionCode(),
                                                                                              null,
                                                                                              null,
                                                                                              date),
                                                                               this.getPage(page));
            return tradeSummaryPage.getContent()
                                   .stream()
                                   .peek(x -> {
                                       if (x.getPublicArea() == null) {
                                           Optional<OpenApiRentInfo> openApiTradeInfo = openApiRentInfoRepository.findById(x.getId());
                                           if (openApiTradeInfo.isPresent()) {
                                               OpenApiRentInfo rentInfo = openApiTradeInfo.get();
                                               x.setPrivateArea(Double.valueOf(rentInfo.getArea()));
                                               x.setPublicArea(Double.valueOf(rentInfo.getArea()));
                                               x.setAreaType((int) (Double.valueOf(rentInfo.getArea()) * 0.3025) + "평");
                                           }
                                       }
                                   })
                                   .map(GuavaTradeResponse::transform)
//                                   // fixme building id값 조회 안하도록
//                                   .peek(x -> x.setBuildingId(String.valueOf(buildingMappingRepository.findByBuildingCode(x
// .getBuildingId())
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
            Page<RentSummary> tradeSummaryPage = rentSummaryRepository.findAll(this.getParams(null,
                                                                                              buildingMapping.getBuildingCode(),
                                                                                              areaId,
                                                                                              date),
                                                                               this.getPage(page));
            return tradeSummaryPage.getContent()
                                   .stream()
                                   .peek(x -> {
                                       if (x.getPublicArea() == null) {
                                           Optional<OpenApiRentInfo> openApiTradeInfo = openApiRentInfoRepository.findById(x.getId());
                                           if (openApiTradeInfo.isPresent()) {
                                               OpenApiRentInfo tradeInfo = openApiTradeInfo.get();
                                               x.setPrivateArea(Double.valueOf(tradeInfo.getArea()));
                                               x.setPublicArea(Double.valueOf(tradeInfo.getArea()));
                                               x.setAreaType((int) (Double.valueOf(tradeInfo.getArea()) * 0.3025) + "평");
                                           }
                                       }
                                   })
                                   .map(GuavaTradeResponse::transform)
//                                   // fixme building id값 조회 안하도록
//                                   .peek(x -> x.setBuildingId(buildingId))
                                   .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private PageRequest getPage(Integer page) {
        return PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "date"));
    }

    private Specification<RentSummary> getParams(String regionCode, String buildingCode, String areaCode, String date) {
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
}
