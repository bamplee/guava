package net.moboo.batch.wooa.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "building_register_tb")
public class BuildingRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String query;
    private Integer rnum;
    private String platPlc;
    @Column(length = 5)
    private String sigunguCd;
    @Column(length = 5)
    private String bjdongCd;
    @Column(length = 1)
    private String platGbCd;
    @Column(length = 4)
    private String bun;
    @Column(length = 4)
    private String ji;
    @Column(length = 33)
    private String mgmBldrgstPk;
    @Column(length = 1)
    private String regstrGbCd;
    @Column(length = 100)
    private String regstrGbCdNm;
    @Column(length = 1)
    private String regstrKindCd;
    @Column(length = 100)
    private String regstrKindCdNm;
    @Column(length = 1)
    private String newOldRegstrGbCd;
    @Column(length = 100)
    private String newOldRegstrGbCdNm;
    @Column(length = 200)
    private String newPlatPlc;
    @Column(length = 100)
    private String bldNm;
    @Column(length = 200)
    private String splotNm;
    @Column(length = 20)
    private String block;
    @Column(length = 20)
    private String lot;
    private Integer bylotCnt;
    @Column(length = 12)
    private String naRoadCd;
    @Column(length = 5)
    private String naBjdongCd;
    @Column(length = 1)
    private String naUgrndCd;
    private Integer naMainBun;
    private Integer naSubBun;
    private Double platArea;
    private Double archArea;
    private Double bcRat;
    private Double totArea;
    private Double vlRatEstmTotArea;
    private Double vlRat;
    @Column(length = 5)
    private String mainPurpsCd;
    @Column(length = 100)
    private String mainPurpsCdNm;
    private String etcPurps;
    private Integer hhldCnt;
    private Integer fmlyCnt;
    private Integer mainBldCnt;
    private Integer atchBldCnt;
    private Double atchBldArea;
    private Integer totPkngCnt;
    private Integer indrMechUtcnt;
    private Double indrMechArea;
    private Integer oudrMechUtcnt;
    private Double oudrMechArea;
    private Integer indrAutoUtcnt;
    private Double indrAutoArea;
    private Integer oudrAutoUtcnt;
    private Double oudrAutoArea;
    @Column(length = 8)
    private String pmsDay;
    @Column(length = 8)
    private String stcnsDay;
    @Column(length = 8)
    private String useAprDay;
    @Column(length = 4)
    private String pmsnoYear;
    @Column(length = 7)
    private String pmsnoKikCd;
    @Column(length = 100)
    private String pmsnoKikCdNm;
    @Column(length = 4)
    private String pmsnoGbCd;
    @Column(length = 100)
    private String pmsnoGbCdNm;
    private Integer hoCnt;
    @Column(length = 4)
    private String engrGrade;
    private Double engrRat;
    private Integer engrEpi;
    @Column(length = 1)
    private String gnBldGrade;
    private Integer gnBldCert;
    @Column(length = 1)
    private String itgBldGrade;
    private Integer itgBldCert;
    @Column(length = 8)
    private String crtnDay;
}
