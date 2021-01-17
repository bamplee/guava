package im.prize.api.datatool;

import im.prize.api.datatool.response.NaverStockOverallResponse;
import im.prize.api.datatool.response.NaverStockResponse;
import im.prize.api.datatool.response.NaverStockSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naverStockClient", url = "https://m.stock.naver.com/")
public interface NaverStockClient {
    @GetMapping("/api/item/getPriceDayList.nhn")
    NaverStockResponse getPriceDayList(@RequestParam("code") String code,
                                       @RequestParam("pageSize") Integer pageSize,
                                       @RequestParam("page") Integer page);

    @GetMapping("/api/json/sise/siseListJson.nhn")
    NaverStockSearchResponse search(@RequestParam("menu") String menu,
                                    @RequestParam("pageSize") Integer pageSize,
                                    @RequestParam("page") Integer page);

    @GetMapping("/api/item/getOverallHeaderItem.nhn")
    NaverStockOverallResponse getOverallHeaderItem(@RequestParam("code") String code);
}
