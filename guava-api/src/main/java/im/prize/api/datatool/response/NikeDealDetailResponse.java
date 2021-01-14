package im.prize.api.datatool.response;

import im.prize.api.domain.entity.constants.DealTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class NikeDealDetailResponse {
    private String seller;
    private String brand;
    private DealTypeEnum type;
    private String title;
    private String subTitle;
    private List<String> imageUrlList;
    private String description;
    private String note;
    private String drawStartDate;
    private String drawEndDate;
    private String noticeDate;
    private String purchaseStartDate;
    private String purchaseEndDate;
    private String date;
    private Integer price;
    private String code;
    private List<Size> sizeList;

    @Data
    public static class Size {
        private String value;
        private Boolean orderable;
    }
}
