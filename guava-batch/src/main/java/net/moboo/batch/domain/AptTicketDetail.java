package net.moboo.batch.domain;

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
public class AptTicketDetail {
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
            private List<Item> item = Lists.newArrayList();

            @Data
            @XmlAccessorType(XmlAccessType.FIELD)
            public static class Item {
                @XmlElement(name = "거래금액")
                private String price;
                @XmlElement(name = "년")
                private String year;
                @XmlElement(name = "단지")
                private String aptName;
                @XmlElement(name = "법정동")
                private String dong;
                @XmlElement(name = "시군구")
                private String sigungu;
                @XmlElement(name = "월")
                private String month;
                @XmlElement(name = "일")
                private String day;
                @XmlElement(name = "전용면적")
                private String area;
                @XmlElement(name = "지번")
                private String lotNumber;
                @XmlElement(name = "지역코드")
                private String regionCode;
                @XmlElement(name = "층")
                private String floor;
            }
        }
    }
}
