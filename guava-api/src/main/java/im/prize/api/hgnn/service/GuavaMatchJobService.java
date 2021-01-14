package im.prize.api.hgnn.service;

import im.prize.api.domain.oboo.AptTemp;
import im.prize.api.domain.oboo.RegionTemp;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;

import java.util.List;

public interface GuavaMatchJobService {
    List<AptTemp> read();

    GuavaRegion processRegion(RegionTemp regionTemp);

    List<GuavaRegion> writeRegion(List<GuavaRegion> guavaRegions);

    GuavaBuilding processBuilding(AptTemp regionTemp);

    List<GuavaBuilding> writeBuilding(List<GuavaBuilding> guavaRegions);
}
