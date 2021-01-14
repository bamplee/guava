package im.prize.api.datatool;

import im.prize.api.datatool.request.GoogleNewsRequest;
import im.prize.api.datatool.response.GoogleNewsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "infoStockClient", url = "https://m.infostock.co.kr")
public interface InfoStockClient {
    @GetMapping("/bord.asp")
    String search(@RequestParam("page") String page,
                      @RequestParam("blockpage") String blockpage,
                      @RequestParam("StartDate") String startDate,
                      @RequestParam("EndDate") String endDate,
                      @RequestParam("keyword") String query);
}
