package net.moboo.batch.wooa.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.moboo.batch.domain.BaseEntity;
import net.moboo.batch.domain.TradeType;

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
@Table(name = "trade_summary_tb")
public class TradeSummary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private TradeType type;
    private String name;
    private Integer price;
    private Integer floor;
    private String date;
    private String areaType;
    private Double privateArea;
    private Double publicArea;

    private String areaCode;
    private String regionCode;
    private String buildingCode;
}
