package net.moboo.batch.wooa.datatool;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kapt-info-api", url = "http://apis.data.go.kr/1611000/AptBasisInfoService")
public interface KaptInfoApiClient {
    @GetMapping(value = "/getAphusBassInfo")
    KaptInfoResponse getAphusBassInfo(@RequestParam("kaptCode") String kaptCode,
                                      @RequestParam("ServiceKey") String serviceKey);
}
