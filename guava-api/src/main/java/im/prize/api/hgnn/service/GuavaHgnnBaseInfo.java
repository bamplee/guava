package im.prize.api.hgnn.service;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GuavaHgnnBaseInfo {
    private ManageCost mangeCost;
    private Integer parkingInside;
    private Integer parkingOutside;
    private Integer parkingTotal;
    private Double parkingRate;
    private Double permissionDate;
    private String approvalDate;
    private Map<String, Object> schedule;
    private List<Object> summary;
    private String homepage;
    private Double slope;
    private Double floorAreaRatio;
    private Double buildingCoverageRatio;
    private Integer buildingCount;
    private Integer floorMax;
    private Integer floorMin;
    private String tel;
    private String id;
    private Integer totalHousehold;
    private Integer totalHouseholdCounted;
    private String earthquake;
    private String company;
    private String asileType;
    private String heatType;
    private String heatSource;

    @Data
    private static class ManageCost {
        private Double year;
        private Double summer;
        private Double winter;
        private String date;
    }
}
