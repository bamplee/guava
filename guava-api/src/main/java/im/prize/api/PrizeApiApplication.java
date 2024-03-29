package im.prize.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableAsync
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
@EntityScan(basePackageClasses = {
    PrizeApiApplication.class
})
public class PrizeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrizeApiApplication.class, args);
    }

}
