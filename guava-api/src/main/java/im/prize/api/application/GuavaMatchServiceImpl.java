package im.prize.api.application;

import im.prize.api.application.dto.Location;
import im.prize.api.datatool.AvokadoKakaoClient;
import im.prize.api.datatool.response.AvokadoKakaoAddressResponse;
import im.prize.api.hgnn.repository.BuildingMapping;
import im.prize.api.hgnn.repository.BuildingMappingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaMatchTemp;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.UnmappingTradeListRepository;
import im.prize.api.interfaces.response.GuavaMatchResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
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
    private final EntityManager entityManager;

    private final BuildingMappingRepository buildingMappingRepository;

    public GuavaMatchServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                 UnmappingTradeListRepository unmappingTradeListRepository,
                                 AvokadoKakaoClient avokadoKakaoClient,
                                 GuavaBuildingRepository guavaBuildingRepository,
                                 EntityManager entityManager, BuildingMappingRepository buildingMappingRepository) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.unmappingTradeListRepository = unmappingTradeListRepository;
        this.avokadoKakaoClient = avokadoKakaoClient;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.entityManager = entityManager;
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
                                     .compareBuildingList(searchGuavaRegion(lng, lat, 1d)
                                                              .stream()
                                                              .filter(y -> y.getType() == 0)
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

    private List<GuavaBuilding> searchGuavaRegion(Double lng, Double lat, Double distance) {
        // 북동쪽 좌표 구하기
        Location northEast = GeometryUtils.calculateByDirection(lat, lng, distance, CardinalDirection.NORTHEAST.getBearing());

        // 남서쪽 좌표 구하기
        Location southWest = GeometryUtils.calculateByDirection(lat, lng, distance, CardinalDirection.SOUTHWEST.getBearing());

        double lng1 = northEast.getLongitude();
        double lat1 = northEast.getLatitude();
        double lng2 = southWest.getLongitude();
        double lat2 = southWest.getLatitude();

        return searchGuavaRegion(lng1, lat1, lng2, lat2);
    }

    private List<GuavaBuilding> searchGuavaRegion(Double lng1, Double lat1, Double lng2, Double lat2) {
        return entityManager.createNativeQuery("SELECT * \n" +
                                                   "FROM guava_building AS r \n" +
                                                   "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + String.format(
            "'LINESTRING(%f %f, %f %f)')",
            lng1,
            lat1,
            lng2,
            lat2) + ", r.point)"
            , GuavaBuilding.class).getResultList();
    }
}
