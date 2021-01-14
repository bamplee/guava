package im.prize.api.hgnn.repository;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "openapi_trade_building_tb")
public class OpenApiTradeBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String regionCode;
    private String sigunguCode;
    private String dongCode;
    private String lotNumber;
    private String aptName;
}
