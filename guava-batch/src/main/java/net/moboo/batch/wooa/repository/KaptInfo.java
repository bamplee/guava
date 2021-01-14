package net.moboo.batch.wooa.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.moboo.batch.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "kapt_info_tb")
public class KaptInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String kaptCode;
    private String kaptName;
    private String kaptAddr;
    private String codeSaleNm;
    private String codeHeatNm;
    private String kaptTarea;
    private String kaptDongCnt;
    private String kaptdaCnt;
    private String kaptBcompany;
    private String kaptAcompany;
    private String kaptTel;
    private String kaptFax;
    private String kaptUrl;
    private String codeAptNm;
    private String doroJuso;
    private String hoCnt;
    private String codeMgrNm;
    private String codeHallNm;
    private String kaptUsedate;
    private String kaptMarea;
    private String kaptMparea_60;
    private String kaptMparea_85;
    private String kaptMparea_135;
    private String kaptMparea_136;
    private String privArea;
    private String bjdCode;
}
