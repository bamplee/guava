package im.prize.api.interfaces.response;

import im.prize.api.application.RegionType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GuavaRegionDetailResponse {
    private RegionType regionType;
    private String type;
    private String name;
    private String tradeCount;
}
