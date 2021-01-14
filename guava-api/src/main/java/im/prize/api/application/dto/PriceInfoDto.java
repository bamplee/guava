package im.prize.api.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PriceInfoDto {
    private String date;
    private Integer price;
    private Integer pointDelta;
    private Double percentageDelta;
}
