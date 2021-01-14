package im.prize.api.hgnn.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class HgnnServiceTest {
    @Autowired
    HgnnService hgnnService;

    @Test
    void sync() {
        hgnnService.sync();
    }

    @Test
    void sync2() {
        hgnnService.sync2();
    }

    @Test
    void sync3() {
        hgnnService.hgnnMatch();
    }
}