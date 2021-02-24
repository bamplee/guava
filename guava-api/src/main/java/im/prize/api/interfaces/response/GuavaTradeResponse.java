package im.prize.api.interfaces.response;

import im.prize.api.application.RentSummary;
import im.prize.api.application.TradeSummary;
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
    private String subPrice;
    private String priceName;
    private String minusPrice;
    private String minusPriceName;
    private String beforeMaxPrice;
    private String beforeMaxPriceName;
    private Boolean isActive;
    private Boolean isNew;
    private Boolean isHighPrice;

    public static GuavaTradeResponse transform(TradeSummary tradeSummary, Integer beforeMaxPrice) {
        LocalDate yyyyMMdd = LocalDate.parse(tradeSummary.getDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));

        GuavaTradeResponseBuilder builder = GuavaTradeResponse.builder()
                .regionId(tradeSummary.getRegionCode())
                .buildingId(tradeSummary.getBuildingCode())
                .type(tradeSummary.getType().getName())
                .name(tradeSummary.getName())
//                                 .address(buildingMapping.getAddress())
                .date(yyyyMMdd.format(DateTimeFormatter.ofPattern("yy.MM.dd")))
                .year(yyyyMMdd.format(DateTimeFormatter.ofPattern("yyyy")))
                .month(yyyyMMdd.format(DateTimeFormatter.ofPattern("MM")))
                .day(yyyyMMdd.format(DateTimeFormatter.ofPattern("dd")))
                .floor(String.valueOf(tradeSummary.getFloor()))
                .price(tradeSummary.getTradePrice())
                .beforeMaxPrice(String.valueOf(beforeMaxPrice))
                .isHighPrice(tradeSummary.getPrice() >= beforeMaxPrice)
                .isNew(tradeSummary.getCreatedDateTime().toLocalDate().equals(LocalDate.now()));

        // fixme
        if (tradeSummary.getAreaType() != null) {
            builder.area(AreaResponse.builder()
                    .areaId(tradeSummary.getAreaCode())
                    .type(tradeSummary.getAreaType().replace("타입", "") + "㎡")
                    .name((int) ((tradeSummary.getPublicArea() != null ? tradeSummary.getPublicArea() : tradeSummary.getPrivateArea()) * 0.3025) + "평")
                    .publicArea(String.valueOf((tradeSummary.getPublicArea() != null ? tradeSummary.getPublicArea() : tradeSummary.getPrivateArea())))
                    .privateArea(String.valueOf(tradeSummary.getPrivateArea()))
                    .build());
        }
        else {
            builder.area(AreaResponse.builder()
                    .areaId("")
                    .type("")
                    .name("")
                    .publicArea("")
                    .privateArea("")
                    .build());
        }

        return builder.build();
    }

    public static GuavaTradeResponse transform(RentSummary rentSummary) {
        LocalDate yyyyMMdd = LocalDate.parse(rentSummary.getDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));

        return GuavaTradeResponse.builder()
                .regionId(rentSummary.getRegionCode())
                .buildingId(rentSummary.getBuildingCode())
                .type(rentSummary.getType().getName())
                .name(rentSummary.getName())
//                                 .address(buildingMapping.getAddress())
                .date(yyyyMMdd.format(DateTimeFormatter.ofPattern("yy.MM.dd")))
                .year(yyyyMMdd.format(DateTimeFormatter.ofPattern("yyyy")))
                .month(yyyyMMdd.format(DateTimeFormatter.ofPattern("MM")))
                .day(yyyyMMdd.format(DateTimeFormatter.ofPattern("dd")))
                .floor(String.valueOf(rentSummary.getFloor()))
                .price(rentSummary.getTradePrice())
                .subPrice(String.valueOf(rentSummary.getSubPrice()))
                .area(AreaResponse.builder()
                        .areaId(rentSummary.getAreaCode())
                        .type(rentSummary.getAreaType())
                        .name((int) (rentSummary.getPublicArea() * 0.3025) + "평")
                        .publicArea(String.valueOf(rentSummary.getPublicArea()))
                        .privateArea(String.valueOf(rentSummary.getPrivateArea()))
                        .build())
//                                 .beforeMaxPrice(b
// eforeHighPrice)
//                                 .isHighPrice(isHighPrice)
                .isNew(rentSummary.getCreatedDateTime().toLocalDate().equals(LocalDate.now()))
                .build();
    }
}
