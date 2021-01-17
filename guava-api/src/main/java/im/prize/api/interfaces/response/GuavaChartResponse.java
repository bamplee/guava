package im.prize.api.interfaces.response;

import im.prize.api.application.TradeSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GuavaChartResponse {
    private String date;
    private String area;
    //    private String floor;
    private String price;

    public static GuavaChartResponse transform(TradeSummary tradeSummary) {
        return GuavaChartResponse.builder()
                                 .date(tradeSummary.getDate())
                                 .area(String.valueOf(tradeSummary.getPrivateArea()))
                                 .price(String.valueOf(tradeSummary.getPrice()))
                                 .build();
    }
}
