package im.prize.api.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyActivityDto {
    private String date;
    private CompanyDto company;
    private PriceInfoDto priceInfo;
}
