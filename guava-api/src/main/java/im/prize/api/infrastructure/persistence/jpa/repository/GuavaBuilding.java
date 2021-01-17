package im.prize.api.infrastructure.persistence.jpa.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "guava_building")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuavaBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String buildingCode;
    private Integer type;
    private String adminRegionCode;
    private String regionCode;
    private String originalName;
    private String name;
    private String address;
    private String roadAddress;
    private Integer portalId;
    private Integer totalHousehold;
    private Double manageCostYear;
    private Double manageCostSummer;
    private Double manageCostWinter;
    private Double floorAreaRatio;
    private Double buildingCoverageRatio;
    private Integer buildingCount;
    private Integer floorMax;
    private Integer floorMin;
    private String tel;
    private Integer parkingTotal;
    private Integer parkingInside;
    private Integer parkingOutside;
    private Double parkingRate;
    private String approvalDate;
    private String baseInfoId;
    private Integer totalHouseholdCounted;
    private String company;
    private String asileType;
    private String heatType;
    private String heatSource;
    private Double rentalBusinessRatio;
    private Integer totalRentalBusinessHousehold;
    private Integer businessCallPrice;
    private String startMonth;
    private Double lat;
    private Double lng;
    private Point point;
    private Double roadviewLat;
    private Double roadviewLng;
    private String diffYearText;
    private String diffYearShortText;
    private String dong;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // (1)
    @JoinColumn(name = "building_key")
    private List<GuavaBuildingArea> areaList;

    public void addArea(GuavaBuildingArea area) {
        if (areaList == null) {
            areaList = new ArrayList<>();
        }
        areaList.add(area);
    }
}
