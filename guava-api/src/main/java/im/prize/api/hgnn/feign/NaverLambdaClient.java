package im.prize.api.hgnn.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naver-lambda-client", url = "https://xak9xzj7k3.apigw.ntruss.com/land/v1/jJu1JRRkIE")
public interface NaverLambdaClient {
    @GetMapping("json")
    NaverLambdaTestResponse getLandDetail(@RequestParam("id") String id, @RequestParam("page") Integer page);
}
