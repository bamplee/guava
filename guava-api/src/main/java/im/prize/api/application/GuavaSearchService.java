package im.prize.api.application;

import im.prize.api.interfaces.response.GuavaSearchResponse;

import java.util.List;

public interface GuavaSearchService {
    List<GuavaSearchResponse> getRegions(String query);

    List<GuavaSearchResponse> getBuildings(String query);

    GuavaSearchResponse getRegion(String regionId);

    GuavaSearchResponse getBuilding(String buildingId);

    List<GuavaSearchResponse> getRegionChilds(String regionCode, RegionType regionType);

    List<GuavaSearchResponse> getPlaceBuildings(String regionId);
}
