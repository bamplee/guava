package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.AptInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AptInfoRepository extends JpaRepository<AptInfo, Long> {
    List<AptInfo> findByAptId(String aptId);
}
