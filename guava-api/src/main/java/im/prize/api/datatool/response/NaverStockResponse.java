package im.prize.api.datatool.response;

import lombok.Data;

import java.util.List;

@Data
public class NaverStockResponse {
    private Result result;

    @Data
    public static class Result {
        private List<Item> list;

        @Data
        public static class Item {
            private String dt;
            private Integer ncv;
            private String rf;
            private Integer cv;
            private Double cr;
            private Integer ov;
            private Integer hv;
            private Integer lv;
            private Integer aq;
        }
    }
}
