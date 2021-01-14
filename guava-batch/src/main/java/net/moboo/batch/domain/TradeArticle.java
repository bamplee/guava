package net.moboo.batch.domain;

import lombok.Data;
import net.moboo.batch.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

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
}
