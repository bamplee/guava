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
@Table(name = "apt_area", indexes = {@Index(columnList = "hgnnId"), @Index(columnList = "portalId"), @Index(columnList = "aptId")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AptArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hgnnId;
    private String portalId;
    private String aptId;
    private String privateArea;
    private String publicArea;
    private String areaType;
    private String totalHousehold;
}
