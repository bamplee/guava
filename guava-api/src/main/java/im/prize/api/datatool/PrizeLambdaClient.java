package im.prize.api.datatool;

import im.prize.api.datatool.request.NikeDealDetailRequest;
import im.prize.api.datatool.request.NikeDealDrawRequest;
import im.prize.api.datatool.request.NikeDealLoginRequest;
import im.prize.api.datatool.request.StockxProductSummaryRequest;
import im.prize.api.datatool.response.NikeDealDetailResponse;
import im.prize.api.datatool.response.NikeDealLoginResponse;
import im.prize.api.datatool.response.NikeDealSummaryResponse;
import im.prize.api.datatool.response.StockxProductSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "prizeLambdaClient", url = "https://m9dnly1lna.execute-api.ap-northeast-2.amazonaws.com/dev/")
public interface PrizeLambdaClient {
    @PostMapping("/nike/login")
    NikeDealLoginResponse login(@RequestBody NikeDealLoginRequest nikeDealLoginRequest);

    @PostMapping("/nike/draw")
    String drawTest(@RequestBody NikeDealDrawRequest nikeDealDrawRequest);

    @GetMapping("/nike/list")
    List<NikeDealSummaryResponse> getNikeDealSummaryList();

    @PostMapping("/nike/detail")
    NikeDealDetailResponse getNikeDealDetail(@RequestBody NikeDealDetailRequest nikeDealDetailRequest);

    @PostMapping("/stockx/detail")
    StockxProductSummaryResponse getStockxProductSummary(@RequestBody StockxProductSummaryRequest stockxProductSummaryRequest);
}
