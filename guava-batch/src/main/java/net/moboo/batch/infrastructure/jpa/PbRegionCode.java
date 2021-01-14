package net.moboo.batch.infrastructure.jpa;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "pb_region_code")
public class PbRegionCode {
    @Id
    private Long code;
    private String name;
    private String status;
}
