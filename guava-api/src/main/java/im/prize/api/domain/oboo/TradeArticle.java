package im.prize.api.domain.oboo;

import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.text.DecimalFormat;

@Data
@Entity
@Table(name = "trade_article")
public class TradeArticle extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String portalId;
    private String type;
    private String regionCode;
    private String buildingCode;
    private String baseDate;
    private String startDate;
    private String endDate;
    private String articleNo;
    private String articleName;
    private String articleStatus;
    private String realEstateTypeCode;
    private String realEstateTypeName;
    private String articleRealEstateTypeCode;
    private String articleRealEstateTypeName;
    private String tradeTypeCode;
    private String tradeTypeName;
    private String verificationTypeCode;
    private String floorInfo;
    private String priceChangeState;
    private Boolean isPriceModification;
    private String dealOrWarrantPrc;
    private String areaName;
    private String area1;
    private String area2;
    private String direction;
    private String articleConfirmYmd;
    private Integer siteImageCount;
    private String articleFeatureDesc;
    private String tagList;
    private String buildingName;
    private Integer sameAddrCnt;
    private Integer sameAddrDirectCnt;
    private String sameAddrMaxPrc;
    private String sameAddrMinPrc;
    private String premiumPrc;
    private String cpid;
    private String cpName;
    private String sellerName;
    private String sellerPhoneNum;
    private String tradeDepositPrice;
    private String tradeRentPrice;
    private String tradeDealPrice;
    private String tradeDayClusterCode;
    private String tradeDayClusterName;
    private String representativeImgThumb;
    private String representativeImgTypeCode;
    private String representativeImgUrl;
    private String cpPcArticleUrl;
    private String cpPcArticleBridgeUrl;
    private Boolean cpPcArticleLinkUseAtArticleTitleYn;
    private Boolean cpPcArticleLinkUseAtCpNameYn;
    private String cpMobileArticleUrl;
    private Boolean cpMobileArticleLinkUseAtArticleTitleYn;
    private Boolean cpMobileArticleLinkUseAtCpNameYn;
    private String latitude;
    private String longitude;
    private Boolean isLocationShow;
    private String rentPrc;
    private String realtorName;
    private String realtorId;
    private String tradeYearMonth;
    private Boolean tradeCheckedByOwner;
    private Boolean isDirectTrade;
    private Boolean isInterest;
    private Boolean isComplex;
    private String detailAddress;
    private String detailAddressYn;
    private String sameAddrPremiumMin;

    public Integer getPrice() {
        String price = this.getDealOrWarrantPrc();
        if (price != null) {
            if (price.contains("억")) {
                price = price.replace(",", "").replace(" ", "");
                String[] splitPrice = price.split("억");
                splitPrice[0] += "0000";
                for (String s : splitPrice) {
                    price = String.valueOf(NumberUtils.toInt(price) + NumberUtils.toInt(s));
                }
            } else if (price.contains("천")) {
                price = String.valueOf(NumberUtils.toDouble(price.replace("천", "")) * 1000);
            } else {
                price = price.replaceAll("[^\\d]", "");
            }
            int intPrice = NumberUtils.toInt(price);
            return intPrice;
        }
        return 0;
    }

    public String getSummaryPrice() {
        String price = this.getDealOrWarrantPrc();
        if (price != null) {
            if (price.contains("억")) {
                price = price.replace(",", "").replace(" ", "");
                String[] splitPrice = price.split("억");
                splitPrice[0] += "0000";
                for (String s : splitPrice) {
                    price = String.valueOf(NumberUtils.toInt(price) + NumberUtils.toInt(s));
                }
            } else if (price.contains("천")) {
                price = String.valueOf(NumberUtils.toDouble(price.replace("천", "")) * 1000);
            } else {
                price = price.replaceAll("[^\\d]", "");
            }
            int intPrice = NumberUtils.toInt(price);
            double result = 0;
            String priceVal = "";
            if (intPrice >= 10000) {
                result = intPrice / 10000.0;
                priceVal = "억";
            } else if (intPrice >= 1000) {
                result = intPrice / 1000.0;
                priceVal = "천";
            } else {
                result = intPrice;
            }
            result = Math.round(result * 10) / 10.0;
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(result) + priceVal;
        }
        return "0";
    }

    public String getTradePrice() {
        String price = this.getDealOrWarrantPrc();
        if (price != null) {
            if (price.contains("억")) {
                price = price.replace(",", "").replace(" ", "");
                String[] splitPrice = price.split("억");
                splitPrice[0] += "0000";
                for (String s : splitPrice) {
                    price = String.valueOf(NumberUtils.toInt(price) + NumberUtils.toInt(s));
                }
            } else if (price.contains("천")) {
                price = String.valueOf(NumberUtils.toDouble(price.replace("천", "")) * 1000);
            }
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
