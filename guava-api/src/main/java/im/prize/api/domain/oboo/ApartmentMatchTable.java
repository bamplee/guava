package im.prize.api.domain.oboo;

import lombok.Data;
import org.locationtech.jts.geom.Point;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "apartment_match_table", indexes = {@Index(columnList = "hgnnRegionCode")})
public class ApartmentMatchTable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String hgnnId;
    private String hgnnRegionCode;
    private String hgnnAptName;
    private String portalId;
    private String dongCode;
    private String dongSigunguCode;
    private String lotNumber;
    private String aptName;
    private Point location;
}
