package im.prize.api.interfaces;

import im.prize.api.application.PlaceService;
import im.prize.api.application.TradeService;
import im.prize.api.application.dto.Location;
import im.prize.api.application.dto.TradeDto;
import im.prize.api.interfaces.response.GuavaBuildingDetailResponse;
import im.prize.api.interfaces.response.PlaceSummaryResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/oboo")
public class ObooController {
    private final PlaceService placeService;
    private final TradeService tradeService;

    public ObooController(PlaceService placeService, TradeService tradeService) {
        this.placeService = placeService;
        this.tradeService = tradeService;
    }

    @GetMapping("/location")
    Location getLocation(@RequestParam("lat") Double lat,
                         @RequestParam("lng") Double lng) {
        return placeService.getLocation(lat, lng);
    }

    @GetMapping("/places")
    List<PlaceSummaryResponse> getPlaces(@RequestParam("lat") Double lat,
                                         @RequestParam("lng") Double lng,
                                         @RequestParam("level") Integer level) {
        return placeService.getPlaces(lat, lng, level);
    }

    @GetMapping("/places/{placeId}")
    GuavaBuildingDetailResponse getPlace(@PathVariable("placeId") String placeId) {
        return placeService.getPlace(placeId);
    }

    @GetMapping("/places/{placeId}/chart")
    List<TradeDto> getChartData(@PathVariable("placeId") String placeId, @RequestParam("beforeMonths") Integer beforeMonths) {
        return tradeService.getChartData(placeId, beforeMonths);
    }

    @GetMapping("/places/{placeId}/trade")
    List<TradeDto> getTradeList(@PathVariable("placeId") String placeId,
                                @RequestParam("page") Integer page,
                                @RequestParam(value = "area", required = false) String area) {
        return tradeService.getTrade(placeId, page, area);
    }

    @GetMapping("/places/{placeId}/forsale")
    List<TradeDto> getForSale(@PathVariable("placeId") String placeId, @RequestParam("page") Integer page) {
        return tradeService.getNaverTrade(placeId, page);
    }
}
