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
public class KaptCodeResponse {
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
        private Body.Items items = new Body.Items();
        private Integer numOfRows;
        private Integer pageNo;
        private Integer totalCount;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Items {
            private List<Body.Items.Item> item = Lists.newArrayList();

            @Data
            @XmlAccessorType(XmlAccessType.FIELD)
            public static class Item {
                @XmlElement(name = "kaptCode")
                private String kaptCode;
                @XmlElement(name = "kaptName")
                private String kaptName;
            }
        }
    }
}
