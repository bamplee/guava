package im.prize.api.interfaces.response;

import im.prize.api.application.RegionType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GuavaSummaryResponse {
    private RegionType type;
    private String id;
    private Double lat;
    private Double lng;
    private String name;
    private String price;
    private String marketPrice;
}
