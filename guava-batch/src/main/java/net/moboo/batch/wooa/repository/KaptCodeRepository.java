package net.moboo.batch.wooa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KaptCodeRepository extends JpaRepository<KaptCode, Long> {
    List<KaptCode> findByKaptCodeIsNull();
    Optional<KaptCode> findByKaptCode(String kaptCode);
}
