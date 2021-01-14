package im.prize.api.datatool.response;

import lombok.Data;

import java.util.List;

@Data
public class YhNewsResponse {
    private String result;
    private Integer pageLast;
    private Integer blockBegin;
    private Integer pageNext;
    private Integer totalPage;
    private Integer pagePrev;
    private Integer totalCount;
    private List<Article> list;

    @Data
    public static class Article {
        private String sequence;
        private String cat;
        private String title;
        private String content;
        private String wdate;
        private String thumbnail;
        private String category;
        private String category_seq;
        private String type;
    }
}
