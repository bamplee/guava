package net.moboo.batch.domain;

import lombok.Data;
import net.moboo.batch.domain.BaseEntity;
import org.apache.commons.lang3.math.NumberUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "trade_item", indexes = {@Index(columnList = "hcpcNo"), @Index(columnList = "startDate"), @Index(columnList = "endDate")})
public class TradeItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String type;
    private String date;
    private String startDate;
    private String endDate;
    private String buildingCode;
    private String regionCode;
    private String hcpcNo;
    private String atclFetrDesc;
    private String atclNm;
    private String atclNo;
    private String atclStatCd;
    private String bildNm;
    private String cfmYmd;
    private Integer cpCnt;
    private String cpNm;
    private String cpid;
    private String directTradYn;
    private String direction;
    private String dtlAddr;
    private String dtlAddrYn;
    private String flrInfo;
    private String prcInfo;
    private String repImgThumb;
    private String repImgTpCd;
    private String repImgUrl;
    private String rletTpCd;
    private String rletTpNm;
    private String rltrNm;
    private Integer sameAddrCnt;
    private Integer sameAddrDirectCnt;
    private String sameAddrHash;
    private String sameAddrMaxPrc;
    private String sameAddrMinPrc;
    private String spc1;
    private String spc2;
    private String tagList;
    private String tradCmplYn;
    private String tradTpCd;
    private String tradTpNm;
    private Boolean tradeCheckedByOwner;
    private String tradePriceHan;
    private String tradePriceInfo;
    private Integer tradeRentPrice;
    private String vrfcTpCd;
    private String sellrNm;
    private String mobileArticleUrl;
    private String mobileArticleLinkTypeCode;
    private Boolean mobileArticleLinkUseAtArticleTitle;
    private Boolean mobileArticleLinkUseAtCpName;
    private String mobileBmsInspectPassYn;
    private Boolean pcArticleLinkUseAtArticleTitle;
    private Boolean pcArticleLinkUseAtCpName;

    public Integer getPrice() {
        String price = this.getPrcInfo();
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
            return intPrice;
        }
        return 0;
    }
}
