package im.prize.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GwanboDataModel {
    private String date;
    private GwanboType gwanboType;
    private String company;
    private String job;
    private String name;
    private String owner;
    private String type;
    private String address;
    private String landArea;
    private String buildingArea;
    private String price;
    private String realPrice;
    private String etc;
}
