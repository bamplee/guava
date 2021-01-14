package net.moboo.batch.infrastructure.feign;

import net.moboo.batch.domain.AptTicketDetail;
import net.moboo.batch.domain.AptTradeDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trade-list-api", url = "http://openapi.molit.go.kr")
public interface AptTradeApiClient {
    @GetMapping(value = "/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTradeDev")
    AptTradeDetail getAptTradeDetailList(@RequestParam("ServiceKey") String serviceKey,
                                         @RequestParam("DEAL_YMD") String dealYmd,
                                         @RequestParam("LAWD_CD") String lawdCd,
                                         @RequestParam("numOfRows") String numOfRows,
                                         @RequestParam("pageNo") String pageNo);

    @GetMapping(value = "/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcSilvTrade")
    AptTicketDetail getAptTicketDetailList(@RequestParam("ServiceKey") String serviceKey,
                                           @RequestParam("DEAL_YMD") String dealYmd,
                                           @RequestParam("LAWD_CD") String lawdCd,
                                           @RequestParam("numOfRows") String numOfRows,
                                           @RequestParam("pageNo") String pageNo);
}
