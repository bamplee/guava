package im.prize.api.interfaces;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GuavaTradeSearch {
    private String regionCode;
    private String buildingCode;
    private String areaCode;
    private String startArea;
    private String endArea;
    private String date;
    private String startDate;
    private String endDate;
}
