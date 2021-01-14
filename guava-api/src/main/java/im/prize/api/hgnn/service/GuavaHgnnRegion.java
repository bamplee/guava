package im.prize.api.hgnn.service;

import lombok.Data;

import java.util.List;

@Data
public class GuavaHgnnRegion {
    private String id;
    private String name;
    private String sido;
    private String sidoName;
    private String sigungu;
    private String sigunguName;
    private String dong;
    private String dongName;
    private String ri;
    private String riName;
    private String date;
    private Integer isExpired;
    private String closeRegion;
    private Double lat;
    private Double lng;
    private String regionCode;
    private String areaRoughly;
    private List<GuavaHgnnBuilding> apts;
    private List<GuavaHgnnBuilding> officetels;
}
