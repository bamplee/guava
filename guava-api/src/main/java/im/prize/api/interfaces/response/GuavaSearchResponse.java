package im.prize.api.interfaces.response;

import im.prize.api.application.RegionType;
import im.prize.api.hgnn.repository.BuildingMapping;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuilding;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaRegion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuavaSearchResponse {
    private RegionType type;
    private String code;
    private String id;
    private String name;
    private String summaryName;
    private String address;
    private Double lat;
    private Double lng;

    private String sidoCode;
    private String sidoName;
    private String sigunguCode;
    private String sigunguName;
    private String dongCode;
    private String dongName;
    private String riCode;
    private String riName;
    private String buildingId;
    private String buildingName;

    public static GuavaSearchResponse transform(GuavaRegion guavaRegion) {
        RegionType regionType = guavaRegion.getRegionType();
        String address = "";
        String name = "";
        String summaryName = "";
        String sigunguName = guavaRegion.getSigunguName();
        String dongName = guavaRegion.getDongName();
        String riName = guavaRegion.getRiName();
        if (regionType == RegionType.RI) {
            summaryName = String.format("%s", guavaRegion.getRiName());
            name = guavaRegion.getRiName();
            address = String.format("%s %s %s", guavaRegion.getSigunguName(), guavaRegion.getDongName(), guavaRegion.getRiName());
        } else if (regionType == RegionType.DONG) {
            summaryName = String.format("%s",
                                        StringUtils.isNotEmpty(guavaRegion.getRiName()) ? guavaRegion.getRiName() :
                                            guavaRegion.getDongName());
            name = StringUtils.isNotEmpty(guavaRegion.getRiName()) ? guavaRegion.getRiName() : guavaRegion.getDongName();
            address = String.format("%s %s %s", guavaRegion.getSigunguName(), guavaRegion.getDongName(), guavaRegion.getRiName());
            if (StringUtils.isNotEmpty(guavaRegion.getRiName())) {
                sigunguName = guavaRegion.getSigunguName() + " " + guavaRegion.getDongName();
                dongName = guavaRegion.getRiName();
                riName = "";
            }
        } else if (regionType == RegionType.SIGUNGU) {
            summaryName = String.format("%s %s", guavaRegion.getSigunguName(), guavaRegion.getDongName());
            name = StringUtils.isNotEmpty(guavaRegion.getDongName()) ? guavaRegion.getDongName() : guavaRegion.getSigunguName();
            address = String.format("%s %s", guavaRegion.getSigunguName(), guavaRegion.getDongName());
            if ("00000".equals(guavaRegion.getDongCode())) {
                sigunguName = guavaRegion.getSigunguName() + " " + guavaRegion.getDongName();
                dongName = guavaRegion.getRiName();
            }
        } else if (regionType == RegionType.SIDO) {
            summaryName = String.format("%s", guavaRegion.getSidoName());
            name = guavaRegion.getSidoName();
            address = String.format("%s", guavaRegion.getSidoName());
        }

        return GuavaSearchResponse.builder()
                                  .code(guavaRegion.getRegionCode())
                                  .type(regionType)
                                  .id(String.valueOf(guavaRegion.getId()))
                                  .lat(guavaRegion.getLat())
                                  .lng(guavaRegion.getLng())
                                  .address(address)
                                  .summaryName(summaryName)
                                  .name(name)
                                  .sidoCode(guavaRegion.getSido())
                                  .sidoName(guavaRegion.getSidoName())
                                  .sigunguCode(guavaRegion.getSigungu())
                                  .sigunguName(sigunguName)
                                  .dongCode(guavaRegion.getDong())
                                  .dongName(dongName)
                                  .riCode(guavaRegion.getRi())
                                  .riName(riName)
                                  .build();
    }

    public static GuavaSearchResponse transform(GuavaRegion guavaRegion, GuavaBuilding guavaBuilding) {
        RegionType regionType = guavaRegion.getRegionType();
        String sigunguName = guavaRegion.getSigunguName();
        String dongName = guavaRegion.getDongName();
        if (guavaRegion.getRegionType() == RegionType.SIGUNGU) {
            if ("00000".equals(guavaRegion.getDongCode())) {
                sigunguName = guavaRegion.getSigunguName() + " " + guavaRegion.getDongName();
                dongName = guavaRegion.getRiName();
            }
        } else if (guavaRegion.getRegionType() == RegionType.DONG) {
            if (StringUtils.isNotEmpty(guavaRegion.getRiName())) {
                sigunguName = guavaRegion.getSigunguName() + " " + guavaRegion.getDongName();
                dongName = guavaRegion.getRiName();
            }
        }
        return GuavaSearchResponse.builder()
                                  .code(guavaRegion.getRegionCode())
                                  .buildingId(String.valueOf(guavaBuilding.getId()))
                                  .buildingName(guavaBuilding.getName())
                                  .type(RegionType.BUILDING)
                                  .id(String.valueOf(guavaRegion.getId()))
                                  .address(guavaBuilding.getAddress())
                                  .summaryName(guavaBuilding.getName())
                                  .name(guavaBuilding.getName())
                                  .lat(guavaBuilding.getLat())
                                  .lng(guavaBuilding.getLng())
                                  .sidoCode(guavaRegion.getSido())
                                  .sidoName(guavaRegion.getSidoName())
                                  .sigunguCode(guavaRegion.getSigungu())
                                  .sigunguName(sigunguName)
                                  .dongCode(guavaRegion.getDong())
                                  .dongName(dongName)
                                  .riCode(guavaRegion.getRi())
                                  .riName(guavaRegion.getRiName())
                                  .build();
    }

    public static GuavaSearchResponse transform(GuavaRegion guavaRegion, BuildingMapping buildingMapping) {
        RegionType regionType = guavaRegion.getRegionType();
        String sigunguName = guavaRegion.getSigunguName();
        String dongName = guavaRegion.getDongName();
        if (guavaRegion.getRegionType() == RegionType.SIGUNGU) {
            if ("00000".equals(guavaRegion.getDongCode())) {
                sigunguName = guavaRegion.getSigunguName() + " " + guavaRegion.getDongName();
                dongName = guavaRegion.getRiName();
            }
        } else if (guavaRegion.getRegionType() == RegionType.DONG) {
            if (StringUtils.isNotEmpty(guavaRegion.getRiName())) {
                sigunguName = guavaRegion.getSigunguName() + " " + guavaRegion.getDongName();
                dongName = guavaRegion.getRiName();
            }
        }
        return GuavaSearchResponse.builder()
                                  .code(guavaRegion.getRegionCode())
                                  .buildingId(String.valueOf(buildingMapping.getId()))
                                  .buildingName(buildingMapping.getBuildingName())
                                  .type(RegionType.BUILDING)
                                  .id(String.valueOf(guavaRegion.getId()))
                                  .address(buildingMapping.getAddress())
                                  .summaryName(buildingMapping.getBuildingName())
                                  .name(buildingMapping.getBuildingName())
                                  .lat(buildingMapping.getPoint().getY())
                                  .lng(buildingMapping.getPoint().getX())
                                  .sidoCode(guavaRegion.getSido())
                                  .sidoName(guavaRegion.getSidoName())
                                  .sigunguCode(guavaRegion.getSigungu())
                                  .sigunguName(sigunguName)
                                  .dongCode(guavaRegion.getDong())
                                  .dongName(dongName)
                                  .riCode(guavaRegion.getRi())
                                  .riName(guavaRegion.getRiName())
                                  .build();
    }
}
