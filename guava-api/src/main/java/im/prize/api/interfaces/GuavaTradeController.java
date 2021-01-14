package im.prize.api.interfaces;

import im.prize.api.application.GuavaTradeService;
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
@RequestMapping("/api/v1/guava/trade")
public class GuavaTradeController {
    private final GuavaTradeService guavaTradeService;

    public GuavaTradeController(GuavaTradeService guavaTradeService) {this.guavaTradeService = guavaTradeService;}

    @GetMapping("/regions/{regionId}")
    List<GuavaTradeResponse> getRegionTrade(@PathVariable("regionId") String regionId,
                                            @RequestParam("page") Integer page,
                                            @RequestParam(value = "startArea", required = false, defaultValue = "0") Integer startArea,
                                            @RequestParam(value = "endArea", required = false, defaultValue = "0") Integer endArea,
                                            @RequestParam(value = "date", required = false) String date) {
        return guavaTradeService.getRegionTrade(regionId, page, startArea, endArea, date);
    }

    @GetMapping("/buildings/{buildingId}")
    List<GuavaTradeResponse> getBuildingTradeList(@PathVariable("buildingId") String buildingId,
                                                  @RequestParam("page") Integer page,
                                                  @RequestParam("areaId") String areaId,
                                                  @RequestParam(value = "date", required = false) String date) {
        return guavaTradeService.getBuildingTradeList(buildingId, page, areaId, date);
    }
}
