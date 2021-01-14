package im.prize.api.datatool;

import im.prize.api.datatool.response.NaverArticleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naverArticleClient",
             url = "https://openapi.naver.com/v1/getRegions/news.json")
public interface NaverArticleClient {
    String DEFAULT_CLIENT_ID = "Qalxf4yGFTqoLaECqKdU";
    String DEFAULT_CLIENT_SECRET = "dL_rhTxlUT";
    String DEFAULT_QUERY = "";
    String DEFAULT_DISPLAY = "100";
    String DEFAULT_START = "1";
    String DEFAULT_SORT = "date";

    @GetMapping
    NaverArticleResponse getArticleList(@RequestHeader(value = "X-Naver-Client-Id", defaultValue = DEFAULT_CLIENT_ID) String clientID,
                                        @RequestHeader(value = "X-Naver-Client-Secret",
                                                       defaultValue = DEFAULT_CLIENT_SECRET) String clientSecret,
                                        @RequestParam(value = "query", defaultValue = DEFAULT_QUERY) String query,
                                        @RequestParam(value = "display", defaultValue = DEFAULT_DISPLAY) Integer display,
                                        @RequestParam(value = "start", defaultValue = DEFAULT_START) Integer start,
                                        @RequestParam(value = "sort", defaultValue = DEFAULT_SORT) String sort);
}
