package im.prize.api.application;

import im.prize.api.infrastructure.persistence.jpa.repository.GuavaMatchTemp;
import im.prize.api.interfaces.response.GuavaMatchResponse;

public interface GuavaMatchService {
    void sync();

    GuavaMatchResponse match();

    GuavaMatchTemp check(String tradeId, String buildingId);
}
