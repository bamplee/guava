package im.prize.api.interfaces.response;

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
}