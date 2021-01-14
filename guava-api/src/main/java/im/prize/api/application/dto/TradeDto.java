package im.prize.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TradeDto {
    private String date;
    private String area;
    private String floor;
    private String price;
}
