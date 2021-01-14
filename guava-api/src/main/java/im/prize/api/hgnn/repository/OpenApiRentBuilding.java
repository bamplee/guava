package im.prize.api.hgnn.repository;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "openapi_rent_building_tb")
public class OpenApiRentBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String regionCode;
    private String sigunguCode;
    private String dong;
    private String lotNumber;
    private String aptName;
}
