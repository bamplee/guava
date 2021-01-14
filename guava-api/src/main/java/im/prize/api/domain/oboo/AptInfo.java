package im.prize.api.domain.oboo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "apt_info", indexes = {@Index(columnList = "hgnnId"), @Index(columnList = "portalId"), @Index(columnList = "aptId")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AptInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hgnnId;
    private String portalId;
    private String aptId;
    private Integer floorAreaRatio;
    private Integer buildingCoverageRatio;
    private Integer buildingCount;
    private Integer floorMax;
    private Integer floorMin;
    private String secretKey;
    private Integer totalHousehold;
    private Integer parkingInside;
    private Integer parkingOutside;
    private String heatType;
    private String heatSource;
    private String asileType;
    private String earthQuake;
    private String startMonth;
}
