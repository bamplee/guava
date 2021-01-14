package im.prize.api.infrastructure.persistence.jpa.repository;

import im.prize.api.domain.entity.PrizeAccount;
import im.prize.api.domain.entity.PrizeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrizeAccountRepository extends JpaRepository<PrizeAccount, Long> {
    List<PrizeAccount> findByUser(PrizeUser user);
}
