package im.prize.api.interfaces.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlaceSummaryResponse {
    private Long id;
    private String name;
    private Double lat;
    private Double lng;
    private String minNaverItemPrice;
    private String maxNaverItemPrice;
    private String minTradePrice;
    private String maxTradePrice;
    private String avgTradePrice;
    private String totalTradeCount;
}
