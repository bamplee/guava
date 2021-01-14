package im.prize.api.interfaces;

import im.prize.api.application.GuavaChartService;
import im.prize.api.interfaces.response.GuavaChartResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/guava/chart")
public class GuavaChartController {
    private final GuavaChartService guavaChartService;

    public GuavaChartController(GuavaChartService guavaChartService) {this.guavaChartService = guavaChartService;}

    @GetMapping("/regions/{regionId}")
    List<GuavaChartResponse> getRegionChartList(@PathVariable("regionId") String regionId,
                                                @RequestParam(value = "startArea", required = false, defaultValue = "0") Integer startArea,
                                                @RequestParam(value = "endArea", required = false, defaultValue = "0") Integer endArea,
                                                @RequestParam(value = "beforeMonth",
                                                              required = false,
                                                              defaultValue = "36") Long beforeMonth) {
        return guavaChartService.getRegionChartList(regionId, startArea, endArea, beforeMonth);
    }

    @GetMapping("/buildings/{buildingId}")
    List<GuavaChartResponse> getChartList(@PathVariable("buildingId") String buildingId,
                                          @RequestParam(value = "areaId", required = false) String areaId,
                                          @RequestParam(value = "beforeMonth", required = false, defaultValue = "36") Long beforeMonth) {
        return guavaChartService.getChartList(buildingId, areaId, beforeMonth);
    }
}