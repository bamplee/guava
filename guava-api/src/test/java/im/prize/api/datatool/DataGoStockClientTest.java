package im.prize.api.datatool;

import im.prize.api.datatool.response.DataGoStockResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataGoStockClientTest {
    @Autowired
    DataGoStockClient dataGoStockClient;
    @Value("${datago.api.key}")
    private String serviceKey;

    @Test
    public void getStkIsinByNmN1() {
        DataGoStockResponse result = dataGoStockClient.getStkIsinByNmN1(serviceKey, "네이버", "2", "1");
        System.out.println(result);
    }

    @Test
    public void getStkIsinByShortIsinN1() {
        DataGoStockResponse result = dataGoStockClient.getStkIsinByShortIsinN1(serviceKey, "035420", "2", "1");
        System.out.println(result);
    }
}