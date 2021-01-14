package im.prize.api.datatool;

import im.prize.api.datatool.response.NaverLoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "naverLoginClient", url = "https://nid.naver.com/")
public interface NaverLoginClient {
    @GetMapping("/oauth2.0/token")
    NaverLoginResponse test(@RequestParam(value = "grant_type", defaultValue = "authorization_code") String grantType,
                            @RequestParam("client_id") String clientId,
                            @RequestParam("client_secret") String clientSecret,
                            @RequestParam("redirect_uri") String redirectURI,
                            @RequestParam("code") String code,
                            @RequestParam("state") String state);
}
