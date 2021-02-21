package im.prize.api.interfaces;

import im.prize.api.application.GuavaMarketService;
import im.prize.api.interfaces.response.GuavaTradeResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/guava/trade/market")
public class GuavaMarketController {
    private final GuavaMarketService guavaMarketService;

    public GuavaMarketController(GuavaMarketService guavaMarketService) {this.guavaMarketService = guavaMarketService;}

    @GetMapping("/regions/{regionId}")
    List<GuavaTradeResponse> getRegionMarketList(@PathVariable("regionId") String regionId,
                                                 @RequestParam("tradeType") String tradeType,
                                                 @RequestParam("page") Integer page,
                                                 @RequestParam(value = "size", required = false, defaultValue = "30") Integer size,
                                                 @RequestParam(value = "startArea", required = false, defaultValue = "0") Integer startArea,
                                                 @RequestParam(value = "endArea", required = false, defaultValue = "0") Integer endArea,
                                                 @RequestParam(value = "date", required = false) String date) {
        return guavaMarketService.getRegionMarketList(tradeType, regionId, page, size, startArea, endArea);
    }

    @GetMapping("/buildings/{buildingId}")
    List<GuavaTradeResponse> getBuildingMarketList(@PathVariable("buildingId") String buildingId,
                                                   @RequestParam("tradeType") String tradeType,
                                                   @RequestParam("page") Integer page,
                                                   @RequestParam(value = "size", required = false, defaultValue = "30") Integer size,
                                                   @RequestParam("areaId") String areaId) {
        return guavaMarketService.getBuildingMarketList(tradeType, buildingId, page, size, areaId);
    }
}
