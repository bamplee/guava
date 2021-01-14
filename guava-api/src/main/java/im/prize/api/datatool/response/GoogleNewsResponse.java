package im.prize.api.datatool.response;

import lombok.Data;

@Data
public class GoogleNewsResponse {
    private String url;
    private String urlToImage;
    private String author;
    private String title;
    private String description;
    private String publishedAt;
}
