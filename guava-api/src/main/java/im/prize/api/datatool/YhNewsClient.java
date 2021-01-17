package im.prize.api.datatool;

import im.prize.api.datatool.response.YhNewsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "yhNewsClient", url = "http://m.yonhapnewstv.co.kr/getRegions/")
public interface YhNewsClient {
    @GetMapping("/getSearchList")
    YhNewsResponse search(@RequestParam("p") String p,
                          @RequestParam("type") String type,
                          @RequestParam("q") String q,
                          @RequestParam("sd") String sd,
                          @RequestParam("ed") String ed,
                          @RequestParam("srt") String srt);
}
