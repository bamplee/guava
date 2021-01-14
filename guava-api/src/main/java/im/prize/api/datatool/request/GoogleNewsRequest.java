package im.prize.api.datatool.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleNewsRequest {
    private String query;
    private String date;
}
