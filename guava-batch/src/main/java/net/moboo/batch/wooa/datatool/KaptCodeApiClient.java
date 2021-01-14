package net.moboo.batch.wooa.datatool;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kapt-code-api", url = "http://apis.data.go.kr/1613000/AptListService1")
public interface KaptCodeApiClient {
    @GetMapping(value = "/getLegaldongAptList")
    KaptCodeResponse getLegaldongAptList(@RequestParam("ServiceKey") String serviceKey,
                                         @RequestParam("bjdCode") String bjdCode,
                                         @RequestParam("pageNo") Integer page,
                                         @RequestParam("numOfRows") Integer size);

    @GetMapping(value = "/getTotalAptList")
    KaptCodeResponse getTotalAptList(@RequestParam("ServiceKey") String serviceKey,
                                     @RequestParam("pageNo") Integer page,
                                     @RequestParam("numOfRows") Integer size);
}
