package net.moboo.batch.hgnn.service;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GuavaHgnnBuilding {
    private String id;
    private Integer type;
    private String adminRegionCode;
    private String regionCode;
    private String name;
    private String address;
    private String roadAddress;
    private Integer portalId;

    private String aliasName;

    private Integer tradeCount;
    private Integer tradeRecentCount;
    private Integer popularity;
    private Integer tradeRate;
    private Integer depositRate;
    private Integer rentRate;
    private String offerDate;
    private Integer totalHousehold;
    private Integer averageRating;

    private GuavaHgnnBaseInfo baseinfo;

    private List<GuavaHgnnPhoto> photos;
    private Integer nearbySchoolPoint;
    private Integer nearbySubwayStationCount;
    private Double floorAreaRatio;
    private Double buildingCoverageRatio;
    private Integer buildingCount;
    private Double rentalBusinessRatio;
    private Integer totalRentalBusinessHousehold;
    private Integer businessCallPrice;
    private Integer tradeType;
    private Integer areaNo;
    private List<Area> area;
    private String startMonth;
    private Double lat;
    private Double lng;
    private Double roadviewLat;
    private Double roadviewLng;
    private String diffYearText;
    private String diffYearShortText;
    private String dong;
    private Boolean isOffer;
    private Dong dongList;

    @Data
    public static class Dong {
        private Long id;
        private String apt;
        private String name;
        private Integer highestFloor;
        private Integer buildingId;
    }

    @Data
    public static class Area {
        private Integer id;
        private Integer totalHousehold;
        private Double privateArea;
        private Double publicArea;
        private String areaType;
        private PopularType popularType;

        @Data
        public static class PopularType {
            private List<PopularTypeBuilding> building;

            @Data
            public static class PopularTypeBuilding {
                private String name;
            }
        }
    }
}
