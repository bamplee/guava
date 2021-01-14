package net.moboo.batch.wooa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findByKaptCodeIsNull();
    List<Building> findByRegionCodeIsNotNull();
}
