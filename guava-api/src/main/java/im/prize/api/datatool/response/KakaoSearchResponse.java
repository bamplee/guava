package im.prize.api.datatool.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class KakaoSearchResponse {
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
        @JsonProperty("address_name")
        private String addressName;
        @JsonProperty("region_1depth_name")
        private String region1depthName;
        @JsonProperty("region_2depth_name")
        private String region2depthName;
        @JsonProperty("region_3depth_name")
        private String region3depthName;
        @JsonProperty("code")
        private String code;
        @JsonProperty("x")
        private Double x;
        @JsonProperty("y")
        private Double y;
    }
}
