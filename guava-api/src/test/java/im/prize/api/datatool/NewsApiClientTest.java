package im.prize.api.datatool;

import im.prize.api.datatool.response.NewsApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class NewsApiClientTest {
    @Value("${news.api.key}")
    private String newsServiceKey;

    @Autowired
    NewsApiClient newsApiClient;

    @Test
    public void getNewsList() {
        NewsApiResponse newsList = newsApiClient.getNewsList("삼성전자", "2020-12-01", "publishedAt", newsServiceKey);
        System.out.println();
    }
}