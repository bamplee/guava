package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.AptArea;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AptAreaRepository extends JpaRepository<AptArea, Long> {
    List<AptArea> findByAptId(String aptId);

    @Cacheable("findByAptIdAndPrivateArea")
    List<AptArea> findByAptIdAndPrivateArea(String aptId, String privateArea);
}
