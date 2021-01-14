package im.prize.api.datatool.response;

import lombok.Data;

import java.util.List;

@Data
public class NaverStockOverallResponse {
    private Result result;
    private String resultCode;

    @Data
    public static class Result {
        private String cd;
        private String nyn;
        private Integer pcv;
        private String ms;
        private Integer nt;
        private String mt;
        private Integer nv;
        private String tyn;
        private Integer lv;
        private String al;
        private Integer my;
        private Integer hv;
        private Double cr;
        private Integer aq;
        private Integer cv;
        private String rf;
        private String time;
        private String nm;
    }
}
