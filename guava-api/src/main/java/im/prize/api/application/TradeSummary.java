package im.prize.api.application;

import im.prize.api.domain.oboo.BaseEntity;
import im.prize.api.domain.oboo.TradeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
