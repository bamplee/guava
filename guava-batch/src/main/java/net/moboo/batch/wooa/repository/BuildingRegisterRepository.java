package net.moboo.batch.wooa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRegisterRepository extends JpaRepository<BuildingRegister, Long> {
    List<BuildingRegister> findByQuery(String query);
}
