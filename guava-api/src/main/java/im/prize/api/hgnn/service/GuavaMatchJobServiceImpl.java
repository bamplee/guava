package im.prize.api.hgnn.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.google.common.collect.Lists;
import im.prize.api.domain.oboo.AptTemp;
import im.prize.api.domain.oboo.RegionTemp;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingArea;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegionRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.AptTempRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GuavaMatchJobServiceImpl implements GuavaMatchJobService {
    private final GuavaRegionRepository guavaRegionRepository;
    private final GuavaBuildingRepository guavaBuildingRepository;
    private final AptTempRepository aptTempRepository;
    private final ObjectMapper objectMapper;

    public GuavaMatchJobServiceImpl(GuavaRegionRepository guavaRegionRepository,
                                    GuavaBuildingRepository guavaBuildingRepository,
                                    AptTempRepository aptTempRepository,
                                    ObjectMapper objectMapper) {
        this.guavaRegionRepository = guavaRegionRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.aptTempRepository = aptTempRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<AptTemp> read() {
        return Lists.newArrayList(aptTempRepository.findAll());
    }

    @Override
    public GuavaBuilding processBuilding(AptTemp aptTemp) {
        try {
            if(aptTemp.getData() != null) {
                JsonNode jsonNode = objectMapper.readValue(aptTemp.getData(), SimpleType.constructUnsafe(JsonNode.class));
                JsonNode aptsNode = jsonNode.get("result").get("data");
                return generate(aptsNode);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<GuavaBuilding> writeBuilding(List<GuavaBuilding> guavaBuildings) {
        log.info("size : {}", guavaBuildings.size());
        return guavaBuildingRepository.saveAll(guavaBuildings);
    }

    @Override
    public GuavaRegion processRegion(RegionTemp regionTemp) {
        try {
            JsonNode jsonNode = objectMapper.readValue(regionTemp.getData(), SimpleType.constructUnsafe(JsonNode.class));
            GuavaRegion guavaRegion = new GuavaRegion();
            JsonNode dataNode = jsonNode.get("data");
            guavaRegion.setRegionCode(dataNode.get("id").textValue());
            guavaRegion.setName(dataNode.get("name").textValue());
            guavaRegion.setSido(dataNode.get("sido").textValue());
            guavaRegion.setSidoName(dataNode.get("sido_name").textValue());
            guavaRegion.setSigungu(dataNode.get("sigungu").textValue());
            guavaRegion.setSigunguName(dataNode.get("sigungu_name").textValue());
            guavaRegion.setDong(dataNode.get("dong").textValue());
            guavaRegion.setDongName(dataNode.get("dong_name").textValue());
            guavaRegion.setRi(dataNode.get("ri").textValue());
            guavaRegion.setRiName(dataNode.get("ri_name").textValue());
            if (dataNode.get("close_region") != null && dataNode.get("close_region").textValue() != null) {
                List<String> closeRegion = objectMapper.readValue(dataNode.get("close_region").textValue(), new TypeReference<List<String>>() {});
//                guavaRegion.setCloseRegion(StringUtils.join(closeRegion, ","));
            }
            guavaRegion.setLat(dataNode.get("lat").doubleValue());
            guavaRegion.setLng(dataNode.get("lng").doubleValue());
            guavaRegion.setPoint(createPoint(guavaRegion.getLng(), guavaRegion.getLat()));
            if (dataNode.get("regionGroup") != null) {
//                guavaRegion.setRegionGroup(dataNode.get("regionGroup").textValue());
            }
            if (dataNode.get("regionCode") != null) {
                guavaRegion.setRegionCode(dataNode.get("regionCode").textValue());
            }
            log.info("id : {}, result : {}", regionTemp.getId(), guavaRegion.toString());
            return guavaRegion;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<GuavaRegion> writeRegion(List<GuavaRegion> guavaRegions) {
        return guavaRegionRepository.saveAll(guavaRegions.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private GuavaBuilding generate(JsonNode jsonNode) {
        GuavaBuilding guavaBuilding = new GuavaBuilding();
        if (jsonNode.get("id") != null) {
            guavaBuilding.setBuildingCode(jsonNode.get("id").textValue());
        }
        if (jsonNode.get("type") != null) {
            guavaBuilding.setType(jsonNode.get("type").intValue());
        }
        if (jsonNode.get("admin_region_code") != null) {
            guavaBuilding.setAdminRegionCode(jsonNode.get("admin_region_code").textValue());
        }
        if (jsonNode.get("region_code") != null) {
            guavaBuilding.setRegionCode(jsonNode.get("region_code").textValue());
        }
        if (jsonNode.get("name") != null) {
            guavaBuilding.setOriginalName(jsonNode.get("name").textValue());
        }
        if (jsonNode.get("name") != null) {
            guavaBuilding.setName(jsonNode.get("name").textValue());
        }
        if (jsonNode.get("address") != null) {
            guavaBuilding.setAddress(jsonNode.get("address").textValue());
        }
        if (jsonNode.get("road_address") != null) {
            guavaBuilding.setRoadAddress(jsonNode.get("road_address").textValue());
        }
        if (jsonNode.get("portal_id") != null) {
            guavaBuilding.setPortalId(jsonNode.get("portal_id").intValue());
        }
        if (jsonNode.get("total_household") != null) {
            guavaBuilding.setTotalHousehold(jsonNode.get("total_household").intValue());
        }
        JsonNode baseInfoNode = jsonNode.get("baseinfo");
        if (baseInfoNode != null) {
            JsonNode costNode = baseInfoNode.get("manage_cost");
            if (costNode != null && costNode.size() > 0) {
                if (costNode.get("year") != null) {
                    guavaBuilding.setManageCostYear(costNode.get("year").doubleValue());
                }
                if (costNode.get("summer") != null) {
                    guavaBuilding.setManageCostSummer(costNode.get("summer").doubleValue());
                }
                if (costNode.get("winter") != null) {
                    guavaBuilding.setManageCostWinter(costNode.get("winter").doubleValue());
                }
            }
            if (baseInfoNode.get("floor_area_ratio") != null) {
//                guavaBuilding.setFloorAreaRatio(baseInfoNode.get("floor_area_ratio").intValue());
            }
            if (baseInfoNode.get("building_coverage_ratio") != null) {
//                guavaBuilding.setBuildingCoverageRatio(baseInfoNode.get("building_coverage_ratio").intValue());
            }
            if (baseInfoNode.get("building_count") != null) {
                guavaBuilding.setBuildingCount(baseInfoNode.get("building_count").intValue());
            }
            if (baseInfoNode.get("floor_max") != null) {
                guavaBuilding.setFloorMax(baseInfoNode.get("floor_max").intValue());
            }
            if (baseInfoNode.get("floor_min") != null) {
                guavaBuilding.setFloorMin(baseInfoNode.get("floor_min").intValue());
            }
            if (baseInfoNode.get("tel") != null) {
                guavaBuilding.setTel(baseInfoNode.get("tel").textValue());
            }
            if (baseInfoNode.get("parking_inside") != null) {
                guavaBuilding.setParkingInside(baseInfoNode.get("parking_inside").intValue());
            }
            if (baseInfoNode.get("parking_outside") != null) {
                guavaBuilding.setParkingOutside(baseInfoNode.get("parking_outside").intValue());
            }
            if (baseInfoNode.get("parking_total") != null) {
                guavaBuilding.setParkingTotal(baseInfoNode.get("parking_total").intValue());
            }
            if (baseInfoNode.get("parking_rate") != null) {
                guavaBuilding.setParkingRate(baseInfoNode.get("parking_rate").doubleValue());
            }
            if (baseInfoNode.get("approval_date") != null) {
                guavaBuilding.setApprovalDate(baseInfoNode.get("approval_date").textValue());
            }
            if (baseInfoNode.get("id") != null) {
                guavaBuilding.setBaseInfoId(baseInfoNode.get("id").textValue());
            }
            if (baseInfoNode.get("total_household") != null) {
                guavaBuilding.setTotalHousehold(baseInfoNode.get("total_household").intValue());
            }
            if (baseInfoNode.get("total_household_counted") != null) {
                guavaBuilding.setTotalHouseholdCounted(baseInfoNode.get("total_household_counted").intValue());
            }
            if (baseInfoNode.get("company") != null) {
                guavaBuilding.setCompany(baseInfoNode.get("company").textValue());
            }
            if (baseInfoNode.get("asile_type") != null) {
                guavaBuilding.setAsileType(baseInfoNode.get("asile_type").textValue());
            }
            if (baseInfoNode.get("heat_type") != null) {
                guavaBuilding.setHeatType(baseInfoNode.get("heat_type").textValue());
            }
            if (baseInfoNode.get("heat_source") != null) {
                guavaBuilding.setHeatSource(baseInfoNode.get("heat_source").textValue());
            }
        }

        if (jsonNode.get("rental_business_ratio") != null) {
//            guavaBuilding.setRentalBusinessRatio(jsonNode.get("rental_business_ratio").intValue());
        }
        if (jsonNode.get("total_rental_business_household") != null) {
            guavaBuilding.setTotalRentalBusinessHousehold(jsonNode.get("total_rental_business_household").intValue());
        }
        if (jsonNode.get("business_call_price") != null) {
            guavaBuilding.setBusinessCallPrice(jsonNode.get("business_call_price").intValue());
        }
        if (jsonNode.get("start_month") != null) {
            guavaBuilding.setStartMonth(jsonNode.get("start_month").textValue());
        }
        if (jsonNode.get("lat") != null) {
            guavaBuilding.setLat(jsonNode.get("lat").doubleValue());
        }
        if (jsonNode.get("lng") != null) {
            guavaBuilding.setLng(jsonNode.get("lng").doubleValue());

        }
        guavaBuilding.setPoint(createPoint(guavaBuilding.getLng(), guavaBuilding.getLat()));

        if (jsonNode.get("roadview_lat") != null) {
            guavaBuilding.setRoadviewLat(jsonNode.get("roadview_lat").doubleValue());
        }
        if (jsonNode.get("roadview_lng") != null) {
            guavaBuilding.setRoadviewLng(jsonNode.get("roadview_lng").doubleValue());
        }
        if (jsonNode.get("diffYearText") != null) {
            guavaBuilding.setDiffYearText(jsonNode.get("diffYearText").textValue());
        }
        if (jsonNode.get("diffYearShortText") != null) {
            guavaBuilding.setDiffYearShortText(jsonNode.get("diffYearShortText").textValue());
        }
        if (jsonNode.get("dong") != null) {
            guavaBuilding.setDong(jsonNode.get("dong").textValue());
        }
//        if (jsonNode.get("moliteId") != null) {
//            guavaBuilding.setMoliteId(jsonNode.get("moliteId").textValue());
//        }
//        if (jsonNode.get("moliteCategory") != null) {
//            guavaBuilding.setMoliteCategory(jsonNode.get("moliteCategory").textValue());
//        }

//        JsonNode areaNode = jsonNode.get("area");
//        if (areaNode != null && areaNode.isArray()) {
//            guavaBuilding.setJsonAreaList(areaNode.toString());
//        }
        JsonNode areaNode = jsonNode.get("area");
        if (areaNode != null && areaNode.isArray()) {
            for (JsonNode node : areaNode) {
                GuavaBuildingArea guavaBuildingArea = new GuavaBuildingArea();
                if (node.get("private_area") != null) {
                    guavaBuildingArea.setPrivateArea(node.get("private_area").doubleValue());
                }
                if (node.get("public_area") != null) {
                    guavaBuildingArea.setPublicArea(node.get("public_area").doubleValue());
                }
                if (node.get("total_household") != null) {
                    guavaBuildingArea.setTotalHousehold(node.get("total_household").intValue());
                }
                if (node.get("area_type") != null) {
                    guavaBuildingArea.setAreaType(node.get("area_type").textValue());
                }
                guavaBuildingArea.setBuildingCode(guavaBuilding.getBuildingCode());
            }
        }
        return guavaBuilding;
    }



    private Point createPoint(double lng, double lat) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPoint(new Coordinate(lng, lat));
    }
}
