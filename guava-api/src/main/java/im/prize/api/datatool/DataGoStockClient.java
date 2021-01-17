package im.prize.api.datatool;

import im.prize.api.datatool.response.DataGoStockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "dataGoStockClient", url = "http://api.seibro.or.kr/openapi/service")
public interface DataGoStockClient {
    @GetMapping("/StockSvc/getStkIsinByNmN1")
    DataGoStockResponse getStkIsinByNmN1(@RequestParam("ServiceKey") String serviceKey,
                                         @RequestParam("secnNm") String secnNm,
                                         @RequestParam("numOfRows") String numOfRows,
                                         @RequestParam("pageNo") String pageNo);

    @GetMapping("/StockSvc/getStkIsinByShortIsinN1")
    DataGoStockResponse getStkIsinByShortIsinN1(@RequestParam("ServiceKey") String serviceKey,
                                                @RequestParam("shortIsin") String shortIsin,
                                                @RequestParam("numOfRows") String numOfRows,
                                                @RequestParam("pageNo") String pageNo);
}
