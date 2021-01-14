package im.prize.api.application.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TradeStatsDto {
    private Double avgPrice;
    private Double maxPrice;
    private Double minPrice;
    private Integer total;
}
