package im.prize.api.infrastructure.persistence.jpa.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "guava_match_temp")
public class GuavaMatchTemp {
    @Id
    private Long id;
    private Long tradeId;
    private Long buildingId;
    private String addressKey;
}
