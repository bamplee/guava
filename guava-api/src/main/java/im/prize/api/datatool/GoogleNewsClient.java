package im.prize.api.datatool;

import im.prize.api.datatool.request.GoogleNewsRequest;
import im.prize.api.datatool.request.NikeDealDetailRequest;
import im.prize.api.datatool.request.NikeDealDrawRequest;
import im.prize.api.datatool.request.NikeDealLoginRequest;
import im.prize.api.datatool.request.StockxProductSummaryRequest;
import im.prize.api.datatool.response.GoogleNewsResponse;
import im.prize.api.datatool.response.NikeDealDetailResponse;
import im.prize.api.datatool.response.NikeDealLoginResponse;
import im.prize.api.datatool.response.NikeDealSummaryResponse;
import im.prize.api.datatool.response.StockxProductSummaryResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(value = "googleNewsClient", url = "https://m9dnly1lna.execute-api.ap-northeast-2.amazonaws.com/dev/")
public interface GoogleNewsClient {
    @PostMapping("/google/news")
    GoogleNewsResponse googleNews(@RequestBody GoogleNewsRequest googleNewsRequest);
}
