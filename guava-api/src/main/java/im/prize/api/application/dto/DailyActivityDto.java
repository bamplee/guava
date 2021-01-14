package im.prize.api.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyActivityDto {
    private String date;
    private CompanyDto company;
    private PriceInfoDto priceInfo;
}
