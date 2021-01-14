package net.moboo.batch.hgnn.feign;

import net.moboo.batch.hgnn.service.GuavaHgnnBuilding;
import net.moboo.batch.hgnn.service.GuavaHgnnResponse;
import net.moboo.batch.hgnn.service.GuavaHgnnResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "hgnnLambdaClient", url = "https://m9dnly1lna.execute-api.ap-northeast-2.amazonaws.com/dev/")
public interface HgnnLambdaClient {
    @PostMapping("/hgnn/summary")
    GuavaHgnnResultResponse<GuavaHgnnBuilding> drawTest(@RequestBody HgnnLambdaRequest hgnnLambdaRequest);
}
