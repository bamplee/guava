package im.prize.api.interfaces.response;

import im.prize.api.application.TradeSummary;
import im.prize.api.hgnn.repository.BuildingMapping;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    public static GuavaTradeResponse transform(BuildingMapping buildingMapping, TradeSummary tradeSummary) {
        LocalDate yyyyMMdd = LocalDate.parse(tradeSummary.getDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));

        return GuavaTradeResponse.builder()
                                 .regionId(tradeSummary.getRegionCode())
                                 .buildingId(String.valueOf(buildingMapping.getId()))
                                 .type(tradeSummary.getType().getName())
                                 .name(tradeSummary.getName())
                                 .address(buildingMapping.getAddress())
                                 .date(yyyyMMdd.format(DateTimeFormatter.ofPattern("yy.MM.dd")))
                                 .year(yyyyMMdd.format(DateTimeFormatter.ofPattern("yyyy")))
                                 .month(yyyyMMdd.format(DateTimeFormatter.ofPattern("MM")))
                                 .day(yyyyMMdd.format(DateTimeFormatter.ofPattern("dd")))
                                 .floor(String.valueOf(tradeSummary.getFloor()))
                                 .price(tradeSummary.getTradePrice())
                                 .area(AreaResponse.builder()
                                                   .areaId(tradeSummary.getAreaCode())
                                                   .type(tradeSummary.getAreaType())
                                                   .name((int) (tradeSummary.getPublicArea() * 0.3025) + "평")
                                                   .publicArea(String.valueOf(tradeSummary.getPublicArea()))
                                                   .privateArea(String.valueOf(tradeSummary.getPrivateArea()))
                                                   .build())
//                                 .beforeMaxPrice(b
// eforeHighPrice)
//                                 .isHighPrice(isHighPrice)
                                 .isNew(tradeSummary.getCreatedDateTime().toLocalDate().equals(LocalDate.now()))
                                 .build();
    }
}
