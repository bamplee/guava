package net.moboo.batch.hgnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuavaBuildingAreaRepository extends JpaRepository<GuavaBuildingArea, Long> {
}
