package net.moboo.batch.hgnn.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "guava_mapping_info")
public class GuavaMappingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer type;
    private String name;
    private String address;
    private String tradeRegionCode;
    private String regionCode;
    private String buildingCode;
    private String portalId;
    private String lotNumber;
}
