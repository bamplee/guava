package im.prize.api.datatool;

import im.prize.api.datatool.response.NaverArticleResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static im.prize.api.datatool.NaverArticleClient.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class NaverArticleClientTest {
    @Autowired
    NaverArticleClient naverArticleClient;

    @Test
    void getArticleList() {
        NaverArticleResponse articleList = naverArticleClient.getArticleList(DEFAULT_CLIENT_ID,
                                                                             DEFAULT_CLIENT_SECRET,
                                                                             "삼성전자",
                                                                             100,
                                                                             1,
                                                                             DEFAULT_SORT);
        System.out.println();
    }
}