package net.moboo.batch.wooa.datatool;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "building-register-api", url = "http://apis.data.go.kr/1613000/BldRgstService_v2")
public interface BuildingRegisterClient {
    @GetMapping(value = "/getBrRecapTitleInfo")
    BuildingRegisterResponse getBrRecapTitleInfo(@RequestParam("ServiceKey") String serviceKey,
                                                 @RequestParam("sigunguCd") String sigunguCd,
                                                 @RequestParam("bjdongCd") String bjdongCd,
                                                 @RequestParam("bun") String bun,
                                                 @RequestParam("ji") String ji,
                                                 @RequestParam("pageNo") Integer page,
                                                 @RequestParam("numOfRows") Integer size);
}
