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
public class KaptInfoResponse {
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
        private Item item = null;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Item {
            @XmlElement(name = "kaptCode")
            private String kaptCode;
            @XmlElement(name = "kaptName")
            private String kaptName;
            @XmlElement(name = "kaptAddr")
            private String kaptAddr;
            @XmlElement(name = "codeSaleNm")
            private String codeSaleNm;
            @XmlElement(name = "codeHeatNm")
            private String codeHeatNm;
            @XmlElement(name = "kaptTarea")
            private String kaptTarea;
            @XmlElement(name = "kaptDongCnt")
            private String kaptDongCnt;
            @XmlElement(name = "kaptdaCnt")
            private String kaptdaCnt;
            @XmlElement(name = "kaptBcompany")
            private String kaptBcompany;
            @XmlElement(name = "kaptAcompany")
            private String kaptAcompany;
            @XmlElement(name = "kaptTel")
            private String kaptTel;
            @XmlElement(name = "kaptFax")
            private String kaptFax;
            @XmlElement(name = "kaptUrl")
            private String kaptUrl;
            @XmlElement(name = "codeAptNm")
            private String codeAptNm;
            @XmlElement(name = "doroJuso")
            private String doroJuso;
            @XmlElement(name = "hoCnt")
            private String hoCnt;
            @XmlElement(name = "codeMgrNm")
            private String codeMgrNm;
            @XmlElement(name = "codeHallNm")
            private String codeHallNm;
            @XmlElement(name = "kaptUsedate")
            private String kaptUsedate;
            @XmlElement(name = "kaptMarea")
            private String kaptMarea;
            @XmlElement(name = "kaptMparea_60")
            private String kaptMparea_60;
            @XmlElement(name = "kaptMparea_85")
            private String kaptMparea_85;
            @XmlElement(name = "kaptMparea_135")
            private String kaptMparea_135;
            @XmlElement(name = "kaptMparea_136")
            private String kaptMparea_136;
            @XmlElement(name = "privArea")
            private String privArea;
            @XmlElement(name = "bjdCode")
            private String bjdCode;
        }
    }
}
