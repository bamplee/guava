package im.prize.api.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuavaMatchTempRepository extends JpaRepository<GuavaMatchTemp, Long> {
    List<GuavaMatchTemp> findByAddressKey(String addressKey);
}
