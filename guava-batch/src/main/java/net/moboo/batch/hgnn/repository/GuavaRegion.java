package net.moboo.batch.hgnn.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.moboo.batch.domain.RegionType;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Point;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public String getSigunguCode() {
        return this.getSido() + this.getSigungu();
    }

    public String getDongCode() {
        return this.getDong() + "00";
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
        if ("000".equals(this.getSigungu())) {
            return RegionType.SIDO;
        }
        if ("000".equals(this.getDong()) && !"000".equals(this.getSigungu())) {
            return RegionType.SIGUNGU;
        }
        if ("000".equals(getRi()) && !"000".equals(this.getDong())) {
            return RegionType.DONG;
        }
        return RegionType.DONG;
    }
}
