package net.moboo.batch.wooa.repository;

import lombok.Data;
import net.moboo.batch.domain.BaseEntity;
import org.locationtech.jts.geom.Point;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "region_tb")
public class Region extends BaseEntity {
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
}
