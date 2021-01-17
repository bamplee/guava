package im.prize.api.application.dto;

import lombok.Data;

@Data
public class TradeStatsDto {
    private Double avgPrice;
    private Double maxPrice;
    private Double minPrice;
    private Integer total;
}
