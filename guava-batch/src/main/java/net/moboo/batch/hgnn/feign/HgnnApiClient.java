package net.moboo.batch.hgnn.feign;

import net.moboo.batch.hgnn.service.GuavaHgnnRegion;
import net.moboo.batch.hgnn.service.GuavaHgnnResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hgnn-api", url = "https://hogangnono.com")
public interface HgnnApiClient {
    @GetMapping("/api/region/{regionCode}/apt")
    String getHgnnRegion(@PathVariable("regionCode") String regionCode);

    @GetMapping("/api/region/{regionCode}/apt")
    GuavaHgnnResponse<GuavaHgnnRegion> getGuavaHgnnRegion(@PathVariable("regionCode") String regionCode);

    @GetMapping("/api/apt/{aptId}/detail")
    String getAptDetail(@RequestHeader("x-hogangnono-event-log") String eventLog,
                        @RequestHeader("x-hogangnono-ct") String ct,
                        @RequestHeader("x-hogangnono-event-duration") String eventDuration,
                        @PathVariable("aptId") String aptId,
                        @RequestParam("aptId") String aptIdParams,
                        @RequestParam("tradeType") String tradeType);
}
