package im.prize.api.datatool;

import im.prize.api.datatool.request.GoogleNewsRequest;
import im.prize.api.datatool.response.GoogleNewsResponse;
import im.prize.api.datatool.response.NaverStockResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoogleNewsClientTest {
    @Autowired
    GoogleNewsClient googleNewsClient;

    @Test
    public void getStkIsinByNmN1() {
        GoogleNewsResponse result = googleNewsClient.googleNews(GoogleNewsRequest.builder().query("네이버").date("20200101").build());
        System.out.println(result);
    }
}