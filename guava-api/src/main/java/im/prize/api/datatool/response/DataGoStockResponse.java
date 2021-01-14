package im.prize.api.datatool.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataGoStockResponse {
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

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Items {
            private List<Item> item = new ArrayList<>();

            @Data
            @XmlAccessorType(XmlAccessType.FIELD)
            public static class Item {
                @XmlElement(name = "eltscYn")
                private String eltscYn;
                @XmlElement(name = "isin")
                private String isin;
                @XmlElement(name = "issuDt")
                private String issuDt;
                @XmlElement(name = "issucoCustno")
                private String issucoCustno;
                @XmlElement(name = "korSecnNm")
                private String korSecnNm;
                @XmlElement(name = "secnKacdNm")
                private String secnKacdNm;
                @XmlElement(name = "shotnIsin")
                private String shotnIsin;
            }
        }
    }
}
