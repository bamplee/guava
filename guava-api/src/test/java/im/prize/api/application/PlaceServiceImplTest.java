package im.prize.api.application;

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
class PlaceServiceImplTest {
    @Autowired
    PlaceService placeService;

    @Test
    void getPlaces() {
//        placeService.getPlaces(37.3220191, 127.0917291, 0.5);
        System.out.println();
    }
}