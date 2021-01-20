package im.prize.api.interfaces;

import im.prize.api.application.GuavaSummaryService;
import im.prize.api.interfaces.response.GuavaBuildingDetailResponse;
import im.prize.api.interfaces.response.GuavaSummaryResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/guava")
public class GuavaController {
    private final GuavaSummaryService guavaService;

    public GuavaController(GuavaSummaryService guavaService) {this.guavaService = guavaService;}

    @GetMapping("/summary")
    List<GuavaSummaryResponse> getRegions(@RequestParam("level") Integer level,
                                          @RequestParam(value = "northEastLng") Double northEastLng,
                                          @RequestParam(value = "northEastLat") Double northEastLat,
                                          @RequestParam(value = "southWestLng") Double southWestLng,
                                          @RequestParam(value = "southWestLat") Double southWestLat,
                                          @RequestParam(value = "startArea", required = false, defaultValue = "0") Integer startArea,
                                          @RequestParam(value = "endArea", required = false, defaultValue = "0") Integer endArea) {
        return guavaService.getSummary(level, northEastLng, northEastLat, southWestLng, southWestLat, startArea, endArea);
    }

    @GetMapping("/regions")

//    @GetMapping("/regions/{regionId}/trade")
//    List<GuavaTradeResponse> getRegionTrade(@PathVariable("regionId") String regionId,
//                                            @RequestParam("page") Integer page,
//                                            @RequestParam(value = "startArea", required = false, defaultValue = "0") Integer startArea,
//                                            @RequestParam(value = "endArea", required = false, defaultValue = "0") Integer endArea,
//                                            @RequestParam(value = "date", required = false) String date) {
//        return guavaService.getRegionTrade(regionId, page, startArea, endArea, date);
//    }

//    @GetMapping("/regions/{regionId}/market")
//    List<GuavaTradeResponse> getRegionMarketList(@PathVariable("regionId") String regionId,
//                                                 @RequestParam("page") Integer page,
//                                                 @RequestParam(value = "startArea", required = false, defaultValue = "0") Integer startArea,
//                                                 @RequestParam(value = "endArea", required = false, defaultValue = "0") Integer endArea,
//                                                 @RequestParam(value = "date", required = false) String date) {
//        return guavaService.getRegionMarketList(regionId, page, startArea, endArea);
//    }

//    @GetMapping("/regions/{regionId}/trade/chart")
//    List<GuavaChartResponse> getRegionChartList(@PathVariable("regionId") String regionId,
//                                                @RequestParam(value = "startArea", required = false, defaultValue = "0") Integer startArea,
//                                                @RequestParam(value = "endArea", required = false, defaultValue = "0") Integer endArea,
//                                                @RequestParam(value = "beforeMonth",
//                                                              required = false,
//                                                              defaultValue = "36") Long beforeMonth) {
//        return guavaService.getRegionChartList(regionId, startArea, endArea, beforeMonth);
//    }

//    @GetMapping("/regions/{regionId}/buildings")
//    List<GuavaSearchResponse> getPlaceBuildings(@PathVariable("regionId") String regionId) {
//        return guavaService.getPlaceBuildings(regionId);
//    }

    @GetMapping("/buildings/{buildingId}/detail")
    GuavaBuildingDetailResponse getBuildingDetail(@PathVariable("buildingId") String buildingId) {
        return guavaService.getBuildingDetail(buildingId);
    }

//    @GetMapping("/buildings/{buildingId}/trade")
//    List<GuavaTradeResponse> getBuildingTradeList(@PathVariable("buildingId") String buildingId,
//                                                  @RequestParam("page") Integer page,
//                                                  @RequestParam("areaId") String areaId,
//                                                  @RequestParam(value = "date", required = false) String date) {
//        return guavaService.getBuildingTradeList(buildingId, page, areaId, date);
//    }

//    @GetMapping("/buildings/{buildingId}/trade/chart")
//    List<GuavaChartResponse> getChartList(@PathVariable("buildingId") String buildingId,
//                                          @RequestParam(value = "areaId", required = false) String areaId,
//                                          @RequestParam(value = "beforeMonth", required = false, defaultValue = "36") Long beforeMonth) {
//        return guavaService.getChartList(buildingId, areaId, beforeMonth);
//    }

//    @GetMapping("/buildings/{buildingId}/market")
//    List<GuavaTradeResponse> getBuildingMarketList(@PathVariable("buildingId") String buildingId,
//                                                   @RequestParam("page") Integer page,
//                                                   @RequestParam("areaId") String areaId) {
//        return guavaService.getBuildingMarketList(buildingId, page, areaId);
//    }

//    @GetMapping("/buildings/getRegions")
//    List<GuavaSearchResponse> getBuildings(@RequestParam("query") String query) {
//        return guavaService.getBuildings(query);
//    }
//
//    @GetMapping("/regions/getRegions")
//    List<GuavaSearchResponse> getRegions(@RequestParam("query") String query) {
//        return guavaService.getRegions(query);
//    }

//    @GetMapping("/regions/current")
//    GuavaSearchResponse getBuildingDetail(@RequestParam("lat") Double lat,
//                                         @RequestParam("lng") Double lng) {
//        return guavaService.getRegion(lat, lng);
//    }
}
