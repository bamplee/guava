package net.moboo.batch.hgnn.feign;

import lombok.Data;

import java.util.List;

@Data
public class NaverPcLandResponse {
    private Boolean isMoreData;
    private Integer mapExposedCount;
    private Boolean nonMapExposedIncluded;
    private List<Article> articleList;

    @Data
    public static class Article {
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
        private List<String> tagList;
        private String buildingName;
        private Integer sameAddrCnt;
        private Integer sameAddrDirectCnt;
        private String sameAddrMaxPrc;
        private String sameAddrMinPrc;
        private String premiumPrc;
        private String cpid;
        private String cpName;
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
        private Boolean tradeCheckedByOwner;
        private Boolean isDirectTrade;
        private Boolean isInterest;
        private Boolean isComplex;
        private String detailAddress;
        private String detailAddressYn;
        private String representativeImgThumb;
        private String representativeImgTypeCode;
        private String representativeImgUrl;
        private String sellerName;
        private String sellerPhoneNum;
        private String tradeYearMonth;
        private String tradeDepositPrice;
        private String tradeRentPrice;
        private String tradeDealPrice;
        private String tradeDayClusterCode;
        private String tradeDayClusterName;
        private String sameAddrPremiumMin;
    }
}
