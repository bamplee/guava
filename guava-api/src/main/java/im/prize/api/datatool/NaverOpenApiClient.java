package im.prize.api.datatool;

import im.prize.api.datatool.response.NaverOpenApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "naverOpenApiClient", url = "https://openapi.naver.com/")
public interface NaverOpenApiClient {
    @GetMapping("/v1/nid/me")
    NaverOpenApiResponse getProfile(@RequestHeader("Authorization") String authorization);
}
