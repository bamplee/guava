package im.prize.api.interfaces.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class GuavaMatchResponse {
    private BuildingInfo originalBuilding;
    private List<BuildingInfo> compareBuildingList;

    @Builder
    @AllArgsConstructor
    @Data
    public static class BuildingInfo {
        private Long id;
        private String address;
        private String name;
        private Double lat;
        private Double lng;
        private String key;
    }
}
