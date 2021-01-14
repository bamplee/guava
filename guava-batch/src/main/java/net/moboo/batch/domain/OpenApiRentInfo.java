package net.moboo.batch.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

@Data
@Entity
@Table(name = "rent_data_tb")
public class OpenApiRentInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 건축년도
    private String since;
    // 년
    private String year;
    // 법정동
    private String dong;
    // 보증금액
    private String price;
    // 아파트
    private String aptName;
    // 월
    private String month;
    // 월세금액
    private String monthlyRent;
    // 일
    private String day;
    // 전용면적
    private String area;
    // 지번
    private String lotNumber;
    // 지역코드
    private String regionCode;
    // 층
    private String floor;
}
