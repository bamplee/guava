package net.moboo.batch.hgnn.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity
@Table(name = "HGNN_APT_TEMP", indexes = {@Index(columnList = "regionCode"), @Index(columnList = "aptId")})
public class AptTemp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String regionCode;
    private String aptId;
    @Lob
    private String data;
    private Boolean isValid;
}