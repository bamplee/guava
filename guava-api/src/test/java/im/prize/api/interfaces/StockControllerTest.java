package im.prize.api.interfaces;

import im.prize.api.application.dto.CompanyActivityDto;
import im.prize.api.application.dto.CompanyDto;
import im.prize.api.application.dto.DailyActivityDto;
import im.prize.api.application.dto.PriceInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class StockControllerTest {
    @Autowired
    StockController stockController;

    @Test
    void getDailyActivities() {
        List<DailyActivityDto> dailyActivities = stockController.getDailyActivities(null, null, null);
        System.out.println();
    }

    @Test
    void getCurrentPriceInfo() {
        PriceInfoDto priceInfo = stockController.getCurrentPriceInfo("035420");
        System.out.println();
    }

    @Test
    void getPrices() {
        List<PriceInfoDto> priceInfos = stockController.getPrices("035420", "20100801");
        System.out.println();
    }

    @Test
    void getCompanyActivities() {
        List<CompanyActivityDto> companyActivities = stockController.getCompanyActivities("035420", "20200801");
        System.out.println();
    }

    @Test
    void getCompanies() {
        List<CompanyDto> companies = stockController.getCompanies("삼성");
        System.out.println();
    }
}