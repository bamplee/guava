package net.moboo.batch.wooa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeSummaryRepository extends JpaRepository<TradeSummary, Long> {
    List<TradeSummary> findByBuildingCode(String buildingCode);
}
