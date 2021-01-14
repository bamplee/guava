package im.prize.api.application;

import im.prize.api.infrastructure.persistence.jpa.repository.GuavaMatchTemp;
import im.prize.api.interfaces.response.GuavaMatchResponse;

public interface GuavaMatchService {
    GuavaMatchResponse match(Integer page);

    GuavaMatchTemp check(String tradeId, String buildingId);
}