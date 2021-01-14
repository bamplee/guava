package net.moboo.batch.hgnn.repository;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "guava_building_area")
public class GuavaBuildingArea {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "building_key")
    private Long buildingKey;
    private String buildingCode;
    private Double privateArea;
    private Double publicArea;
    private Integer totalHousehold;
    private String areaType;
}
