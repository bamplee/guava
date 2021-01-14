package im.prize.api.interfaces.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GuavaBuildingDetailResponse {
    private Long id;
    private String regionId;
    private String name;
    private Double lat;
    private Double lng;
    private String address;
    private Integer floorAreaRatio;
    private Integer hoCount;
    private Integer dongCount;
    private String aptType;
    private Integer buildingCoverageRatio;
    private Integer buildingCount;
    private Integer maxFloor;
    private Integer minFloor;
    private String since;
    private Integer parkingInside;
    private Integer parkingOutside;
    private Integer parkingTotal;
    private List<AreaResponse> areaList;
}