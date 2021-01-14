package net.moboo.batch.hgnn.feign;

import lombok.Data;

import java.util.List;

@Data
public class NaverLandResponse {
    private Result result;

    @Data
    public static class Result {
        private String moreDataYn;
        private Boolean showGuarantee;
        private Integer totAtclCnt;
        private List<Item> list;

        @Data
        public static class Item {
            private String atclFetrDesc;
            private String atclNm;
            private String atclNo;
            private String atclStatCd;
            private String bildNm;
            private String cfmYmd;
            private Integer cpCnt;
            private CpLinkVO cpLinkVO;
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
            private List<String> tagList;
            private String tradCmplYn;
            private String tradTpCd;
            private String tradTpNm;
            private Boolean tradeCheckedByOwner;
            private String tradePriceHan;
            private String tradePriceInfo;
            private Integer tradeRentPrice;
            private String vrfcTpCd;
            private String sellrNm;

            @Data
            public static class CpLinkVO {
                private String cpId;
                private String mobileArticleUrl;
                private String mobileArticleLinkTypeCode;
                private Boolean mobileArticleLinkUseAtArticleTitle;
                private Boolean mobileArticleLinkUseAtCpName;
                private String mobileBmsInspectPassYn;
                private Boolean pcArticleLinkUseAtArticleTitle;
                private Boolean pcArticleLinkUseAtCpName;
            }
        }
    }
}
