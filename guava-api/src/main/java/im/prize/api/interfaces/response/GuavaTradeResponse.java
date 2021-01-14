package im.prize.api.interfaces.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GuavaTradeResponse {
    private Boolean isRent;
    private String buildingId;
    private String regionId;
    private String type;
    private String date;
    private String year;
    private String month;
    private String day;
    private String name;
    private String dongName;
    private String address;
    private AreaResponse area;
    private String floor;
    private String price;
    private String priceName;
    private String minusPrice;
    private String minusPriceName;
    private String beforeMaxPrice;
    private String beforeMaxPriceName;
    private Boolean isActive;
    private Boolean isNew;
    private Boolean isHighPrice;
}
