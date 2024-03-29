package im.prize.api.infrastructure.persistence.jpa.repository;

import im.prize.api.application.RegionType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Point;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "guava_region")
public class GuavaRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String regionCode;
    private String name;
    private String sido;
    private String sidoName;
    private String sigungu;
    private String sigunguName;
    private String dong;
    private String dongName;
    private String ri;
    private String riName;
    private Double lat;
    private Double lng;
    private Point point;
    private String closeRegionIdCsv;
    private String buildingIdCsv;
    private Boolean isActive;

    public String getSigunguCode() {
        return this.getSido() + this.getSigungu();
    }

    public String getDongCode() {
        return this.getDong() + this.getRi();
    }

    public String getDisplayName() {
        String name = this.getName();
        if (StringUtils.isNotEmpty(this.getSidoName())) {
            name = this.getSidoName();
        }
        if (StringUtils.isNotEmpty(this.getSigunguName())) {
            name = this.getSigunguName();
        }
        if (StringUtils.isNotEmpty(this.getDongName())) {
            name = this.getDongName();
        }
        if (StringUtils.isNotEmpty(this.getRiName())) {
            name = this.getRiName();
        }
        return name;
    }

    public RegionType getRegionType() {
        if (!"00".equals(this.getRi()) && !"".equals(this.getRi())) {
            return RegionType.RI;
        }
        if (!"000".equals(this.getDong()) && "00".equals(this.getRi())) {
            return RegionType.DONG;
        }
        if ("000".equals(this.getSigungu())) {
            return RegionType.SIDO;
        }
        if ("000".equals(this.getDong())) {
            return RegionType.SIGUNGU;
        }

        return RegionType.RI;
    }

    public String getValidRegionCode() {
        String result = this.getSido();
        if (!"000".equals(this.getSigungu())) {
            result += this.getSigungu();
        }
        if (!"000".equals(this.getDong())) {
            result += this.getDong();
        }
        if (!"00".equals(this.getRi())) {
            result += this.getRi();
        }
        return result;
    }
}
