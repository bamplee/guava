package im.prize.api.datatool.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StockxProductSummaryRequest {
    private String code;
}
