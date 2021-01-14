package im.prize.api.datatool.response;

import lombok.Data;

import java.util.List;

@Data
public class NaverStockSearchResponse {
    private Result result;

    @Data
    public static class Result {
        private Integer totCnt;
        private String ms;
        private List<Item> itemList;

        @Data
        public static class Item {
            private String cd;
            private String nm;
            private Integer nv;
            private Integer cv;
            private Double cr;
            private String rf;
            private Integer pcv;
            private Integer mks;
            private Integer aq;
            private Integer aa;
            private String ms;
        }
    }
}
