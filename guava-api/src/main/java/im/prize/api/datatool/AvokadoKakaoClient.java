package im.prize.api.datatool;

import im.prize.api.datatool.response.AvokadoKakaoAddressResponse;
import im.prize.api.datatool.response.KakaoSearchResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "AvokadoKakaoClient", url = "https://dapi.kakao.com/v2")
public interface AvokadoKakaoClient {
    @Cacheable("kakao-address")
    @GetMapping("/local/geo/coord2address.json")
    AvokadoKakaoAddressResponse address(@RequestHeader("Authorization") String authorization,
                                        @RequestParam("x") Double longitude,
                                        @RequestParam("y") Double latitude);

    @Cacheable("kakao-search")
    @GetMapping("/local/search/address.json")
    AvokadoKakaoAddressResponse search(@RequestHeader("Authorization") String authorization,
                                       @RequestParam("query") String query,
                                       @RequestParam("page") Integer page,
                                       @RequestParam("size") Integer size);

    @Cacheable("kakao-coord2regioncode")
    @GetMapping("/local/geo/coord2regioncode.json")
    KakaoSearchResponse coord2regioncode(@RequestHeader("Authorization") String authorization,
                                         @RequestParam("x") Double x,
                                         @RequestParam("y") Double y);
}
