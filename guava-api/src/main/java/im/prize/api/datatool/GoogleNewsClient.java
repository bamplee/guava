package im.prize.api.datatool;

import im.prize.api.datatool.request.GoogleNewsRequest;
import im.prize.api.datatool.response.GoogleNewsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "googleNewsClient", url = "https://m9dnly1lna.execute-api.ap-northeast-2.amazonaws.com/dev/")
public interface GoogleNewsClient {
    @PostMapping("/google/news")
    GoogleNewsResponse googleNews(@RequestBody GoogleNewsRequest googleNewsRequest);
}
