package im.prize.api.interfaces.response;

import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingArea;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class AreaResponse {
    private String areaId;
    private String type;
    private String publicArea;
    private String privateArea;
    private String hoCount;
    private String name;

    public static Optional<AreaResponse> transform(GuavaBuildingArea guavaBuildingArea) {
        if (guavaBuildingArea == null || guavaBuildingArea.getId() == null) {
            return Optional.empty();
        }
        return Optional.of(AreaResponse.builder()
                                       .areaId(String.valueOf(guavaBuildingArea.getId()))
                                       .type(guavaBuildingArea.getAreaType().replace("타입", "") + "㎡")
                                       .privateArea(String.valueOf(guavaBuildingArea.getPrivateArea()))
                                       .publicArea(String.valueOf(guavaBuildingArea.getPublicArea()))
                                       .name((int) (guavaBuildingArea.getPublicArea() * 0.3025) + "평")
                                       .hoCount(String.valueOf(guavaBuildingArea.getTotalHousehold()))
                                       .build());
    }
}
