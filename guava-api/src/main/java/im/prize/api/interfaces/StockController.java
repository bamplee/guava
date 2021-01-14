package im.prize.api.interfaces;

import im.prize.api.application.StockService;
import im.prize.api.application.dto.CompanyActivityDto;
import im.prize.api.application.dto.CompanyDto;
import im.prize.api.application.dto.DailyActivityDto;
import im.prize.api.application.dto.PriceInfoDto;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {this.stockService = stockService;}

    @GetMapping("/companies")
    List<CompanyDto> getCompanies(@RequestParam(value = "query") String query) {
        return stockService.getCompanyList(query);
    }

    @GetMapping("/activities")
    List<DailyActivityDto> getDailyActivities(@RequestParam(value = "query", required = false) String query,
                                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                                              @RequestParam(value = "size", defaultValue = "30") Integer size) {
        return stockService.getActivityList(query, page, size);
    }

    @GetMapping("/companies/{companyId}/prices/current")
    PriceInfoDto getCurrentPriceInfo(@PathVariable("companyId") String companyId) {
        return stockService.getCurrentPriceInfo(companyId);
    }

    @GetMapping("/companies/{companyId}/prices")
    List<PriceInfoDto> getPrices(@PathVariable("companyId") String companyId, @RequestParam("since") String since) {
        return stockService.getPrices(companyId, since);
    }

    @GetMapping("/companies/{companyId}/activities")
    List<CompanyActivityDto> getCompanyActivities(@PathVariable("companyId") String companyId, @RequestParam("since") String since) {
        return stockService.getCompanyActivities(companyId, since);
    }
}
