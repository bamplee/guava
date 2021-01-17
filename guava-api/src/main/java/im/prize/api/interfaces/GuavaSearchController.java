package im.prize.api.interfaces;

import im.prize.api.application.GuavaSearchService;
import im.prize.api.application.RegionType;
import im.prize.api.interfaces.response.GuavaSearchResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/guava/search")
public class GuavaSearchController {
    private final GuavaSearchService guavaSearchService;

    public GuavaSearchController(GuavaSearchService guavaSearchService) {this.guavaSearchService = guavaSearchService;}

    @GetMapping
    List<GuavaSearchResponse> search(@RequestParam("query") String query) {
        return guavaSearchService.search(query);
    }

    @GetMapping("/buildings")
    List<GuavaSearchResponse> getBuildings(@RequestParam("query") String query) {
        return guavaSearchService.getBuildings(query);
    }

    @GetMapping("/regions")
    List<GuavaSearchResponse> getRegions(@RequestParam("query") String query) {
        return guavaSearchService.getRegions(query);
    }

    @GetMapping("/regions/{placeId}")
    GuavaSearchResponse getRegion(@PathVariable("placeId") String placeId) {
        return guavaSearchService.getRegion(placeId);
    }

    @GetMapping("/regions/{regionId}/buildings")
    List<GuavaSearchResponse> getPlaceBuildings(@PathVariable("regionId") String regionId) {
        return guavaSearchService.getPlaceBuildings(regionId);
    }

    @GetMapping("/buildings/{placeId}")
    GuavaSearchResponse getBuilding(@PathVariable("placeId") String placeId) {
        return guavaSearchService.getBuilding(placeId);
    }

    @GetMapping("/places/childs")
    List<GuavaSearchResponse> getPlaceChilds(@RequestParam("regionCode") String regionCode,
                                             @RequestParam("regionType") RegionType regionType) {
        return guavaSearchService.getRegionChilds(regionCode, regionType);
    }
}
