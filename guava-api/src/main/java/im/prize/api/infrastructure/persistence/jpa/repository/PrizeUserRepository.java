package im.prize.api.infrastructure.persistence.jpa.repository;

import im.prize.api.domain.entity.PrizeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrizeUserRepository extends JpaRepository<PrizeUser, Long> {
    Optional<PrizeUser> findByLoginId(String name);
}
