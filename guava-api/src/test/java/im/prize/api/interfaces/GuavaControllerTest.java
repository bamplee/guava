package im.prize.api.interfaces;

import im.prize.api.interfaces.response.GuavaSummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class GuavaControllerTest {
    @Autowired
    GuavaController guavaController;

    @Test
    void getLocation() {
    }
}