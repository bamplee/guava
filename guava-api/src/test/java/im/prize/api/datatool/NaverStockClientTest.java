package im.prize.api.datatool;

import im.prize.api.datatool.response.NaverStockOverallResponse;
import im.prize.api.datatool.response.NaverStockResponse;
import im.prize.api.datatool.response.NaverStockSearchResponse;
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
public class NaverStockClientTest {
    @Autowired
    NaverStockClient naverStockClient;

    @Test
    public void getStkIsinByNmN1() {
        NaverStockResponse result = naverStockClient.getPriceDayList("035420", 1, 1);
        System.out.println(result);
    }

    @Test
    public void getTopSearchList() {
        NaverStockSearchResponse result = naverStockClient.search("top_search", 1, 1);
        System.out.println(result);
    }

    @Test
    public void getOverallHeaderItem() {
        NaverStockOverallResponse overallHeaderItem = naverStockClient.getOverallHeaderItem("035420");
        System.out.println();
    }
}