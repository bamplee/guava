package im.prize.api.hgnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KaptInfoRepository extends JpaRepository<KaptInfo, Long> {
    List<KaptInfo> findByKaptNameIsNull();

    Optional<KaptInfo> findByKaptCode(String kaptCode);
}
