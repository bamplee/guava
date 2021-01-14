package im.prize.api.datatool.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NikeDealDrawRequest {
    private String id;
    private String password;
    private String url;
    private String size;
    private String defaultSize;
}
