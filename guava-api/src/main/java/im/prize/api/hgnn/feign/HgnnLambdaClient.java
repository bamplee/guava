package im.prize.api.hgnn.feign;

import im.prize.api.hgnn.service.GuavaHgnnBuilding;
import im.prize.api.hgnn.service.GuavaHgnnResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "hgnnLambdaClient", url = "https://m9dnly1lna.execute-api.ap-northeast-2.amazonaws.com/dev/")
public interface HgnnLambdaClient {
    @PostMapping("/hgnn/summary")
    GuavaHgnnResultResponse<GuavaHgnnBuilding> drawTest(@RequestBody HgnnLambdaRequest hgnnLambdaRequest);

    @PostMapping("/hgnn/summary")
    Map<String, Object> test2(@RequestBody HgnnLambdaRequest hgnnLambdaRequest);
}
