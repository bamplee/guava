package im.prize.api.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import im.prize.api.hgnn.repository.BuildingMapping;
import im.prize.api.hgnn.repository.BuildingMappingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingSpecs;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.interfaces.GuavaBuildingSearch;
import im.prize.api.interfaces.response.GuavaSearchResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GuavaSearchServiceImpl implements GuavaSearchService {
    private static final Integer PAGE_START_NO = 0;
    private static final Integer PAGE_SIZE = 100;
    @Value("${app.kakao.apiKey}")
    private String kakaoMapApiKey;
    private final GuavaRegionRepository guavaRegionRepository;
    private final GuavaBuildingRepository guavaBuildingRepository;
    private final BuildingMappingRepository buildingMappingRepository;
    private final ObjectMapper objectMapper;

    public GuavaSearchServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                  GuavaBuildingRepository guavaBuildingRepository,
                                  BuildingMappingRepository buildingMappingRepository,
                                  ObjectMapper objectMapper) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.buildingMappingRepository = buildingMappingRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<GuavaSearchResponse> search(String query) {
        List<GuavaSearchResponse> regions = this.getRegions(query);
        List<GuavaSearchResponse> buildings = this.getBuildings(query);
        return Stream.concat(regions.stream(), buildings.stream()).collect(Collectors.toList());
    }

    @Override
    public List<GuavaSearchResponse> getRegions(String query) {
        Pageable limit = PageRequest.of(PAGE_START_NO, 5);
        Page<GuavaRegion> guavaRegionPage = guavaRegionRepository.findAll(GuavaRegionRepository.search(query), limit);
        return guavaRegionPage.get().map(GuavaSearchResponse::transform).collect(Collectors.toList());
    }

    @Override
    public List<GuavaSearchResponse> getBuildings(String query) {
        Pageable limit = PageRequest.of(PAGE_START_NO, 10);
        String likeQuery = "%" + StringUtils.join(query.replaceAll(" ", "").split(""), "%") + "%";
        List<BuildingMapping> buildingsByAddress = buildingMappingRepository.search(likeQuery);
//        Page<GuavaBuilding> buildingsByAddress = guavaBuildingRepository.findAll(this.getParams(query, query), limit);

        return buildingsByAddress.stream()
                                 .filter(x -> x.getType().equals(PAGE_START_NO))
                                 .map(x -> GuavaSearchResponse.transform(guavaRegionRepository.findByRegionCode(x.getRegionCode()).get(),
                                                                         x))
                                 .collect(Collectors.toList());
    }

    @Override
    public GuavaSearchResponse getRegion(String regionId) {
        Optional<GuavaRegion> byId = guavaRegionRepository.findById(Long.valueOf(regionId));
        GuavaRegion guavaRegion = byId.get();
        return GuavaSearchResponse.transform(guavaRegion);
    }

    @Override
    public GuavaSearchResponse getBuilding(String buildingCode) {
//        Optional<GuavaBuilding> byBuildingCode = guavaBuildingRepository.findById(Long.valueOf(buildingCode));
        Optional<BuildingMapping> optionalBuildingMapping = buildingMappingRepository.findById(Long.valueOf(buildingCode));
        if (optionalBuildingMapping.isPresent()) {
            Optional<GuavaRegion> byRegionCode = guavaRegionRepository.findByRegionCode(optionalBuildingMapping.get().getRegionCode());
            if (byRegionCode.isPresent()) {
                GuavaRegion guavaRegion = byRegionCode.get();
//                GuavaBuilding guavaBuilding = byBuildingCode.get();
                BuildingMapping buildingMapping = optionalBuildingMapping.get();
                return GuavaSearchResponse.transform(guavaRegion, buildingMapping);
            }
        }
        return GuavaSearchResponse.builder().build();
    }

    @Override
    public List<GuavaSearchResponse> getRegionChilds(String regionCode, RegionType regionType) {
        List<GuavaRegion> guavaRegionList;
        if (regionType == RegionType.SIDO) {
            guavaRegionList = guavaRegionRepository.findBySigungu("000");
        } else if (regionType == RegionType.DONG) {
            guavaRegionList = guavaRegionRepository.findByRegionCodeLikeAndIsActiveIsNotNull(regionCode + "%");
        } else {
            guavaRegionList = guavaRegionRepository.findByRegionCodeLike(regionCode + "%");
        }
        return guavaRegionList.stream()
                              .filter(x -> x.getRegionType() == regionType)
                              .map(GuavaSearchResponse::transform)
                              .collect(Collectors.toList());
    }

    @Override
    public List<GuavaSearchResponse> getPlaceBuildings(String regionId) {
        Optional<GuavaRegion> optionalGuavaRegion = guavaRegionRepository.findById(Long.valueOf(regionId));
        if (optionalGuavaRegion.isPresent()) {
            GuavaRegion guavaRegion = optionalGuavaRegion.get();
            return guavaBuildingRepository.findByRegionCode(guavaRegion.getRegionCode()).stream()
                                          .filter(x -> x.getType() == 0)
                                          .map(x -> GuavaSearchResponse.transform(guavaRegion, x))
                                          .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private Specification<GuavaBuilding> getParams(String name,
                                                   String address) {
        Map<String, Object> paramsMap = objectMapper.convertValue(GuavaBuildingSearch.builder()
                                                                                     .name(name)
                                                                                     .address(address)
                                                                                     .build(), Map.class);
        Map<GuavaBuildingSpecs.SearchKey, Object> params = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            if (ObjectUtils.isNotEmpty(entry.getValue()) && StringUtils.isNotEmpty(String.valueOf(entry.getValue()))) {
                params.put(GuavaBuildingSpecs.SearchKey.convert(entry.getKey()), entry.getValue());
            }
        }
        return GuavaBuildingSpecs.searchWith(params);
    }
}
