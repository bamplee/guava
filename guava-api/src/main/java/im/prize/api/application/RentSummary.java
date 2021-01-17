package im.prize.api.application;

import im.prize.api.domain.oboo.BaseEntity;
import im.prize.api.domain.oboo.TradeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.text.DecimalFormat;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "rent_summary_tb")
public class RentSummary extends BaseEntity {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private TradeType type;
    private String name;
    private Integer price;
    private Integer subPrice;
    private Integer floor;
    private String date;
    private String areaType;
    private Double privateArea;
    private Double publicArea;

    private String areaCode;
    private String regionCode;
    private String buildingCode;

    public String getSummaryPrice() {
        String price = String.valueOf(this.getPrice());
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
        String price = String.valueOf(this.getPrice());
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
