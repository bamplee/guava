package im.prize.api.domain.oboo;

import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.text.DecimalFormat;

@Data
@Entity
@Table(name = "openapi_trade_info",
       indexes = {@Index(columnList = "year"),
                  @Index(columnList = "month"),
                  @Index(columnList = "aptName"),
                  @Index(columnList = "dongSigunguCode"),
                  @Index(columnList = "dongCode")})
public class OpenApiTradeInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // 일련번호
    private String serialNumber;
    // 년
    private String year;
    // 월
    private String month;
    // 일
    private String day;
    // 건축년도
    private String since;
    // 아파트
    private String aptName;
    // 전용면적
    private Double area;
    // 층
    private String floor;
    // 거래금액
    private String price;
    // 도로명
    private String road;
    // 도로명건물본번호코드
    private String roadMainCode;
    // 도로명건물부번호코드
    private String roadSubCode;
    // 도로명시군구코드
    private String roadSigunguCode;
    // 도로명일련번호코드
    private String roadSerialNumberCode;
    // 도로명지상지하코드
    private String roadGroundCode;
    // 도로명코드
    private String roadCode;
    // 법정동
    private String dong;
    // 법정동본번코드
    private String dongMainCode;
    // 법정동부번코드
    private String dongSubCode;
    // 법정동시군구코드
    private String dongSigunguCode;
    // 법정동읍면동코드
    private String dongCode;
    // 법정동지번코드
    private String dongLotNumberCode;
    // 지번
    private String lotNumber;
    // 지역코드
    private String regionCode;
    private String date;
    private String buildingId;

    public String getSummaryPrice() {
        String price = this.getPrice();
        if (price != null) {
            price = price.replace(",", "");
            int intPrice = NumberUtils.toInt(price);
            double result = 0;
            String priceVal = "";
            if (intPrice >= 10000) {
                result = intPrice / 10000.0;
                priceVal = "억";
            } else if (intPrice >= 1000) {
                result = intPrice / 1000.0;
                priceVal = "천";
            }
            result = Math.round(result * 10) / 10.0;
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(result) + priceVal;
        }
        return "0";
    }

    public String getTradePrice() {
        String price = this.getPrice();
        if (price != null) {
            price = price.replace(",", "");
            int intPrice = NumberUtils.toInt(price);
            double result = 0;
            String priceVal = "";
            if (intPrice >= 10000) {
                result = intPrice / 10000.0;
                priceVal = "억";
            } else if (intPrice >= 1000) {
                result = intPrice / 1000.0;
                priceVal = "천";
            }
            result = Math.round(result * 100) / 100.0;
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(result) + priceVal;
        }
        return "0";
    }
}
