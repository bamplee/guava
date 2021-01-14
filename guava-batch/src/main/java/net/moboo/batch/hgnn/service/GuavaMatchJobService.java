package net.moboo.batch.hgnn.service;

import net.moboo.batch.hgnn.repository.AptTemp;
import net.moboo.batch.hgnn.repository.GuavaBuilding;
import net.moboo.batch.hgnn.repository.GuavaRegion;
import net.moboo.batch.hgnn.repository.RegionTemp;

import java.util.List;

public interface GuavaMatchJobService {
    List<AptTemp> read();

    GuavaRegion processRegion(RegionTemp regionTemp);

    List<GuavaRegion> writeRegion(List<GuavaRegion> guavaRegions);

    GuavaBuilding processBuilding(AptTemp regionTemp);

    List<GuavaBuilding> writeBuilding(List<GuavaBuilding> guavaRegions);
}
