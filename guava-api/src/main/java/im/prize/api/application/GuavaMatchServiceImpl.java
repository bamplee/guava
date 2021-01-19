package im.prize.api.application;

import im.prize.api.datatool.AvokadoKakaoClient;
import im.prize.api.datatool.response.AvokadoKakaoAddressResponse;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaMatchTemp;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.UnmappingTradeList;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.UnmappingTradeListRepository;
import im.prize.api.interfaces.response.GuavaMatchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
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

    public GuavaMatchServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                 UnmappingTradeListRepository unmappingTradeListRepository,
                                 AvokadoKakaoClient avokadoKakaoClient,
                                 GuavaBuildingRepository guavaBuildingRepository) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.unmappingTradeListRepository = unmappingTradeListRepository;
        this.avokadoKakaoClient = avokadoKakaoClient;
        this.guavaBuildingRepository = guavaBuildingRepository;
    }

    @Override
    public GuavaMatchResponse match(Integer page) {
        Optional<UnmappingTradeList> byBuildingIdIsNull = unmappingTradeListRepository.findByBuildingIdIsNull(PageRequest.of(page, PAGE_SIZE))
                                                                                      .getContent()
                                                                                      .stream()
                                                                                      .findFirst();

        Optional<GuavaMatchResponse> guavaMatchResponse = byBuildingIdIsNull.map(x -> {
            String address = guavaRegionRepository.findByRegionCode(x.getDongSigunguCode() + x.getDongCode())
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
                                                          .name(x.getAptName())
                                                          .lat(lat)
                                                          .lng(lng)
                                                          .key(x.getAptName() + x.getLotNumber() + x.getDongCode() + x.getDongSigunguCode())
                                                          .build())
                                     .compareBuildingList(guavaBuildingRepository.findByRegionCode(x.getDongSigunguCode() + x
                                         .getDongCode())
                                                                                 .stream()
                                                                                 .map(y -> GuavaMatchResponse.BuildingInfo
                                                                                     .builder()
                                                                                     .id(y.getId())
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
    public GuavaMatchTemp check(String tradeId, String buildingId) {
        Optional<UnmappingTradeList> originalData = unmappingTradeListRepository.findById(Long.valueOf(tradeId));
        Optional<GuavaBuilding> buildingData = guavaBuildingRepository.findById(Long.valueOf(buildingId));
        if (originalData.isPresent()) {
            UnmappingTradeList updateData = originalData.get();
            if (buildingData.isPresent()) {
                GuavaBuilding guavaBuilding = buildingData.get();
                updateData.setBuildingId(guavaBuilding.getBuildingCode());
                unmappingTradeListRepository.save(updateData);
            } else {
                updateData.setBuildingId("-1");
                unmappingTradeListRepository.save(updateData);
            }
        }
        return GuavaMatchTemp.builder().build();
    }

}
