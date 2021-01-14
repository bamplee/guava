package im.prize.api.datatool.response;

import lombok.Data;

@Data
public class StockxProductSummaryResponse {
    private String productCode;
    private Integer retailPrice;
    private Integer resalePrice;
}
