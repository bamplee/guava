package im.prize.api.datatool.response;

import im.prize.api.domain.entity.constants.DealTypeEnum;
import lombok.Data;

@Data
public class NikeDealSummaryResponse {
    private String seller;
    private String brand;
    private DealTypeEnum type;
    private String date;
    private String url;
    private String imageUrl;
    private String code;
}
