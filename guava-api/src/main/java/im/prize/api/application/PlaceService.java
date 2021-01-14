package im.prize.api.application;

import im.prize.api.application.dto.Location;
import im.prize.api.interfaces.response.GuavaBuildingDetailResponse;
import im.prize.api.interfaces.response.PlaceSummaryResponse;

import java.util.List;

public interface PlaceService {
    Location getLocation(Double lat, Double lng);

    GuavaBuildingDetailResponse getPlace(String placeId);

    List<PlaceSummaryResponse> getPlaces(Double lat, Double lng, Integer level);
}
