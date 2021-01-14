package im.prize.api.datatool.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class NaverArticleResponse {
    private String rss;
    private String channel;
    private String lastBuildDate;
    private Integer total;
    private Integer start;
    private Integer display;
    private List<Item> items;

    @Data
    public static class Item {
        private String title;
        private String originallink;
        private String link;
        private String description;
        private Date pubDate;
    }
}
