package im.prize.api.application;

import im.prize.api.datatool.AvokadoKakaoClient;
import im.prize.api.datatool.response.AvokadoKakaoAddressResponse;
import im.prize.api.hgnn.repository.BuildingMapping;
import im.prize.api.hgnn.repository.BuildingMappingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaMatchTemp;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.UnmappingTradeList;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.UnmappingTradeListRepository;
import im.prize.api.interfaces.response.GuavaMatchResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class GuavaMatchServiceImpl implements GuavaMatchService {
    private static final Integer PAGE_SIZE = 30;

    @Value("${app.kakao.apiKey}")
    private String kakaoMapApiKey;

    private final GuavaRegionRepository guavaRegionRepository;
    private final UnmappingTradeListRepository unmappingTradeListRepository;
    private final AvokadoKakaoClient avokadoKakaoClient;
    private final GuavaBuildingRepository guavaBuildingRepository;

    private final BuildingMappingRepository buildingMappingRepository;

    public GuavaMatchServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                 UnmappingTradeListRepository unmappingTradeListRepository,
                                 AvokadoKakaoClient avokadoKakaoClient,
                                 GuavaBuildingRepository guavaBuildingRepository,
                                 BuildingMappingRepository buildingMappingRepository) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.unmappingTradeListRepository = unmappingTradeListRepository;
        this.avokadoKakaoClient = avokadoKakaoClient;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.buildingMappingRepository = buildingMappingRepository;
    }

    @Override
    public GuavaMatchResponse match() {
        List<BuildingMapping> list = buildingMappingRepository.findByBuildingCodeIsNull();
        Random r = new Random();
        Optional<BuildingMapping> byBuildingIdIsNull = list.stream()
                                                           .skip(r.nextInt(list.size() - 1)).findFirst();
//        Optional<UnmappingTradeList> byBuildingIdIsNull = unmappingTradeListRepository.findByBuildingIdIsNull(PageRequest.of(page,
// PAGE_SIZE))
//                                                                                      .getContent()
//                                                                                      .stream()
//                                                                                      .findFirst();

        Optional<GuavaMatchResponse> guavaMatchResponse = byBuildingIdIsNull.map(x -> {
            String address = guavaRegionRepository.findByRegionCode(x.getRegionCode())
                                                  .map(GuavaRegion::getName)
                                                  .orElse("") + " " + x.getLotNumber();
            AvokadoKakaoAddressResponse search = avokadoKakaoClient.search(kakaoMapApiKey, address, 0, 10);
            double lat = 0d;
            double lng = 0d;
            if (search.getMeta().getTotalCount() > 0) {
                lat = search.getDocuments().get(0).getY();
                lng = search.getDocuments().get(0).getX();
            }
            return GuavaMatchResponse.builder()
                                     .originalBuilding(
                                         GuavaMatchResponse
                                             .BuildingInfo.builder()
                                                          .id(x.getId())
                                                          .address(address)
                                                          .name(x.getBuildingName())
                                                          .lat(lat)
                                                          .lng(lng)
                                                          .build())
                                     .compareBuildingList(guavaBuildingRepository.findByRegionCode(x.getRegionCode())
                                                                                 .stream()
                                                                                 .map(y -> GuavaMatchResponse.BuildingInfo
                                                                                     .builder()
                                                                                     .id(y.getId())
                                                                                     .buildingCode(y.getBuildingCode())
                                                                                     .address(y.getAddress())
                                                                                     .name(y.getName())
                                                                                     .lat(y.getLat())
                                                                                     .lng(y.getLng())
                                                                                     .build())
                                                                                 .collect(Collectors.toList()))
                                     .build();
        });
        return guavaMatchResponse.orElse(null);
    }

    @Override
    public GuavaMatchTemp check(String tradeId, String buildingCode) {
        Optional<BuildingMapping> originalData = buildingMappingRepository.findById(Long.valueOf(tradeId));
        BuildingMapping buildingMapping = originalData.get();
        buildingMapping.setBuildingCode(buildingCode);
        if (StringUtils.isNotEmpty(buildingCode)) {
            Optional<GuavaBuilding> byBuildingCode = guavaBuildingRepository.findByBuildingCode(buildingCode);
            if (byBuildingCode.isPresent()) {
                GuavaBuilding guavaBuilding = byBuildingCode.get();
                buildingMapping.setAddress(guavaBuilding.getAddress());
                buildingMapping.setPoint(guavaBuilding.getPoint());
                buildingMapping.setPortalId(String.valueOf(guavaBuilding.getPortalId()));
                buildingMapping.setType(guavaBuilding.getType());
            }
        }
        buildingMappingRepository.save(buildingMapping);

        return GuavaMatchTemp.builder().build();
    }

}
