package im.prize.api.datatool.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AvokadoKakaoAddressResponse {
    private Meta meta;
    private List<Document> documents;

    @Data
    public static class Meta {
        @JsonProperty("total_count")
        private Integer totalCount;
    }

    @Data
    public static class Document {
        @JsonProperty("region_type")
        private String regionType;
        @JsonProperty("region_1depth_name")
        private String addressName;
        @JsonProperty("address_type")
        private String addressType;
        @JsonProperty("x")
        private Double x;
        @JsonProperty("y")
        private Double y;

        @JsonProperty("road_address")
        private RoadAddress roadAddress;
        private Address address;

        @Data
        public static class RoadAddress {
            @JsonProperty("address_name")
            private String addressName;
            @JsonProperty("region_1depth_name")
            private String region1depthName;
            @JsonProperty("region_2depth_name")
            private String region2depthName;
            @JsonProperty("region_3depth_name")
            private String region3depthName;
            @JsonProperty("road_name")
            private String roadName;
            @JsonProperty("underground_yn")
            private String undergroundYn;
            @JsonProperty("main_building_no")
            private String main_buildingNo;
            @JsonProperty("sub_building_no")
            private String sub_buildingNo;
            @JsonProperty("building_name")
            private String buildingName;
            @JsonProperty("zone_no")
            private String zoneNo;
        }

        @Data
        public static class Address {
            @JsonProperty("address_name")
            private String addressName;
            @JsonProperty("region_1depth_name")
            private String region1depthName;
            @JsonProperty("region_2depth_name")
            private String region2depthName;
            @JsonProperty("region_3depth_name")
            private String region3depthName;
            @JsonProperty("mountain_yn")
            private String mountainYn;
            @JsonProperty("main_address_no")
            private String mainAddressNo;
            @JsonProperty("sub_address_no")
            private String subAddressNo;
            @JsonProperty("h_code")
            private String hCode;
            @JsonProperty("b_code")
            private String bCode;
            @JsonProperty("x")
            private Double x;
            @JsonProperty("y")
            private Double y;
        }
    }
}
