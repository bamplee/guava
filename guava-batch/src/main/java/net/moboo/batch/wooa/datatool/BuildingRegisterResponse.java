package net.moboo.batch.wooa.datatool;

import com.google.common.collect.Lists;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class BuildingRegisterResponse {
    private Header header = new Header();
    private Body body = new Body();

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Header {
        private String resultCode = "";
        private String resultMsg = "";
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {
        private Items items = new Items();
        private Integer numOfRows;
        private Integer pageNo;
        private Integer totalCount;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Items {
            private List<Item> item = Lists.newArrayList();

            @Data
            @XmlAccessorType(XmlAccessType.FIELD)
            public static class Item {
                @XmlElement(name = "rnum")
                private Integer rnum;
                @XmlElement(name = "platPlc")
                private String platPlc;
                @XmlElement(name = "sigunguCd")
                private String sigunguCd;
                @XmlElement(name = "bjdongCd")
                private String bjdongCd;
                @XmlElement(name = "platGbCd")
                private String platGbCd;
                @XmlElement(name = "bun")
                private String bun;
                @XmlElement(name = "ji")
                private String ji;
                @XmlElement(name = "mgmBldrgstPk")
                private String mgmBldrgstPk;
                @XmlElement(name = "regstrGbCd")
                private String regstrGbCd;
                @XmlElement(name = "regstrGbCdNm")
                private String regstrGbCdNm;
                @XmlElement(name = "regstrKindCd")
                private String regstrKindCd;
                @XmlElement(name = "regstrKindCdNm")
                private String regstrKindCdNm;
                @XmlElement(name = "newOldRegstrGbCd")
                private String newOldRegstrGbCd;
                @XmlElement(name = "newOldRegstrGbCdNm")
                private String newOldRegstrGbCdNm;
                @XmlElement(name = "newPlatPlc")
                private String newPlatPlc;
                @XmlElement(name = "bldNm")
                private String bldNm;
                @XmlElement(name = "splotNm")
                private String splotNm;
                @XmlElement(name = "block")
                private String block;
                @XmlElement(name = "lot")
                private String lot;
                @XmlElement(name = "bylotCnt")
                private Integer bylotCnt;
                @XmlElement(name = "naRoadCd")
                private String naRoadCd;
                @XmlElement(name = "naBjdongCd")
                private String naBjdongCd;
                @XmlElement(name = "naUgrndCd")
                private String naUgrndCd;
                @XmlElement(name = "naMainBun")
                private Integer naMainBun;
                @XmlElement(name = "naSubBun")
                private Integer naSubBun;
                @XmlElement(name = "platArea")
                private Double platArea;
                @XmlElement(name = "archArea")
                private Double archArea;
                @XmlElement(name = "bcRat")
                private Double bcRat;
                @XmlElement(name = "totArea")
                private Double totArea;
                @XmlElement(name = "vlRatEstmTotArea")
                private Double vlRatEstmTotArea;
                @XmlElement(name = "vlRat")
                private Double vlRat;
                @XmlElement(name = "mainPurpsCd")
                private String mainPurpsCd;
                @XmlElement(name = "mainPurpsCdNm")
                private String mainPurpsCdNm;
                @XmlElement(name = "etcPurps")
                private String etcPurps;
                @XmlElement(name = "hhldCnt")
                private Integer hhldCnt;
                @XmlElement(name = "fmlyCnt")
                private Integer fmlyCnt;
                @XmlElement(name = "mainBldCnt")
                private Integer mainBldCnt;
                @XmlElement(name = "atchBldCnt")
                private Integer atchBldCnt;
                @XmlElement(name = "atchBldArea")
                private Double atchBldArea;
                @XmlElement(name = "totPkngCnt")
                private Integer totPkngCnt;
                @XmlElement(name = "indrMechUtcnt")
                private Integer indrMechUtcnt;
                @XmlElement(name = "indrMechArea")
                private Double indrMechArea;
                @XmlElement(name = "oudrMechUtcnt")
                private Integer oudrMechUtcnt;
                @XmlElement(name = "oudrMechArea")
                private Double oudrMechArea;
                @XmlElement(name = "indrAutoUtcnt")
                private Integer indrAutoUtcnt;
                @XmlElement(name = "indrAutoArea")
                private Double indrAutoArea;
                @XmlElement(name = "oudrAutoUtcnt")
                private Integer oudrAutoUtcnt;
                @XmlElement(name = "oudrAutoArea")
                private Double oudrAutoArea;
                @XmlElement(name = "pmsDay")
                private String pmsDay;
                @XmlElement(name = "stcnsDay")
                private String stcnsDay;
                @XmlElement(name = "useAprDay")
                private String useAprDay;
                @XmlElement(name = "pmsnoYear")
                private String pmsnoYear;
                @XmlElement(name = "pmsnoKikCd")
                private String pmsnoKikCd;
                @XmlElement(name = "pmsnoKikCdNm")
                private String pmsnoKikCdNm;
                @XmlElement(name = "pmsnoGbCd")
                private String pmsnoGbCd;
                @XmlElement(name = "pmsnoGbCdNm")
                private String pmsnoGbCdNm;
                @XmlElement(name = "hoCnt")
                private Integer hoCnt;
                @XmlElement(name = "engrGrade")
                private String engrGrade;
                @XmlElement(name = "engrRat")
                private Double engrRat;
                @XmlElement(name = "engrEpi")
                private Integer engrEpi;
                @XmlElement(name = "gnBldGrade")
                private String gnBldGrade;
                @XmlElement(name = "gnBldCert")
                private Integer gnBldCert;
                @XmlElement(name = "itgBldGrade")
                private String itgBldGrade;
                @XmlElement(name = "itgBldCert")
                private Integer itgBldCert;
                @XmlElement(name = "crtnDay")
                private String crtnDay;
            }
        }
    }
}
