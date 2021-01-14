package net.moboo.batch.wooa.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.wooa.datatool.BuildingRegisterClient;
import net.moboo.batch.wooa.datatool.BuildingRegisterResponse;
import net.moboo.batch.wooa.datatool.KaptCodeApiClient;
import net.moboo.batch.wooa.datatool.KaptCodeResponse;
import net.moboo.batch.wooa.datatool.KaptInfoApiClient;
import net.moboo.batch.wooa.datatool.KaptInfoResponse;
import net.moboo.batch.wooa.repository.BuildingRegister;
import net.moboo.batch.wooa.repository.BuildingRegisterRepository;
import net.moboo.batch.wooa.repository.BuildingRepository;
import net.moboo.batch.wooa.repository.KaptCode;
import net.moboo.batch.wooa.repository.KaptCodeRepository;
import net.moboo.batch.wooa.repository.KaptInfo;
import net.moboo.batch.wooa.repository.KaptInfoRepository;
import net.moboo.batch.wooa.repository.RegionRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class KaptServiceImpl implements KaptService {
    @Value("${rent-list-api.api.key}")
    private String serviceKey;

    private final RegionRepository regionRepository;
    private final KaptCodeApiClient kaptCodeApiClient;
    private final KaptCodeRepository kaptCodeRepository;
    private final KaptInfoRepository kaptInfoRepository;
    private final KaptInfoApiClient kaptInfoApiClient;
    private final BuildingRepository buildingRepository;
    private final BuildingRegisterRepository buildingRegisterRepository;
    private final BuildingRegisterClient buildingRegisterClient;

    public KaptServiceImpl(RegionRepository regionRepository,
                           KaptCodeApiClient kaptCodeApiClient,
                           KaptCodeRepository kaptCodeRepository,
                           KaptInfoRepository kaptInfoRepository,
                           KaptInfoApiClient kaptInfoApiClient,
                           BuildingRepository buildingRepository,
                           BuildingRegisterRepository buildingRegisterRepository,
                           BuildingRegisterClient buildingRegisterClient) {
        this.regionRepository = regionRepository;
        this.kaptCodeApiClient = kaptCodeApiClient;
        this.kaptCodeRepository = kaptCodeRepository;
        this.kaptInfoRepository = kaptInfoRepository;
        this.kaptInfoApiClient = kaptInfoApiClient;
        this.buildingRepository = buildingRepository;
        this.buildingRegisterRepository = buildingRegisterRepository;
        this.buildingRegisterClient = buildingRegisterClient;
    }

    @Override
    public List<KaptCode> read(String regionCode) {
        return null;
    }

    @Override
    public List<KaptCode> readAll() {
//        return kaptCodeRepository.findByKaptCodeIsNull()
//                               .stream()
////                               .filter(x -> !"000".equals(x.getDong()))
//                               .map(x -> {
//                                   log.info(x.toString());
//                                   try {
//                                       List<KaptCodeResponse.Body.Items.Item> items = Lists.newArrayList();
//                                       int page = 1;
//                                       while (true) {
//                                           KaptCodeResponse kaptCodeResponse =
//                                               kaptCodeApiClient.getLegaldongAptList(
//                                                   serviceKey,
//                                                   x.getRegionCode(),
//                                                   page++,
//                                                   100);
//
//                                           List<KaptCodeResponse.Body.Items.Item> result =
//                                               kaptCodeResponse.getBody()
//                                                               .getItems()
//                                                               .getItem();
//                                           if ("00".equals(kaptCodeResponse.getHeader().getResultCode())) {
//                                               items.addAll(result);
//                                           }
//                                           if (result.size() == 0) {
//                                               break;
//                                           }
//                                       }
//                                       return items.stream()
//                                                   .filter(y -> !kaptCodeRepository.findByKaptCode(y.getKaptCode()).isPresent())
//                                                   .map(y -> this.transform(x.getRegionCode(), y))
//                                                   .collect(Collectors.toList());
//                                   } catch (Exception e) {
//                                       log.error(e.getMessage());
//                                       return Lists.newArrayList(KaptCode.builder().regionCode(x.getRegionCode()).build());
//                                   }
//                               })
//                               .filter(Objects::nonNull)
//                               .flatMap(Collection::stream)
//                               .collect(Collectors.toList());
//        return kaptCodeRepository.findByKaptCodeIsNull();
        List<String> kaptCodes = kaptInfoRepository.findAll().stream().map(KaptInfo::getKaptCode).collect(Collectors.toList());
        return kaptCodeRepository.findAll()
                                 .stream()
                                 .filter(x -> !kaptCodes.contains(x.getKaptCode()))
//                                 .map(this::transform)
                                 .collect(
                                     Collectors.toList());
    }

    private KaptCode transform(KaptCodeResponse.Body.Items.Item y) {
        return KaptCode.builder().kaptCode(y.getKaptCode()).kaptName(y.getKaptName()).build();
    }

    private KaptCode transform(String regionCode, KaptCodeResponse.Body.Items.Item y) {
        return KaptCode.builder().regionCode(regionCode).kaptCode(y.getKaptCode()).kaptName(y.getKaptName()).build();
    }

    @Override
    public KaptCode process(KaptCode kaptCode) {
        return null;
    }

    @Override
    public List<KaptCode> write(List<KaptCode> kaptCodeList) {
        return kaptCodeRepository.saveAll(kaptCodeList);
    }

    @Override
    public List<KaptCode> readAllByKaptInfo() {
        return null;
    }

    @Override
    public KaptInfo processByKaptInfo(KaptCode kaptCode) {
        log.info("code : {}", kaptCode);
        try {
            KaptInfoResponse aphusBassInfo = kaptInfoApiClient.getAphusBassInfo(kaptCode.getKaptCode(), serviceKey);
            KaptInfoResponse.Body.Item item = aphusBassInfo.getBody().getItem();
            if (item != null) {
                Optional<KaptInfo> byKaptCode = kaptInfoRepository.findByKaptCode(kaptCode.getKaptCode());
                return byKaptCode.map(kaptInfo -> KaptInfo.builder()
                                                          .id(kaptInfo.getId())
                                                          .kaptCode(item.getKaptCode())
                                                          .kaptName(item.getKaptName())
                                                          .kaptAddr(item.getKaptAddr())
                                                          .codeSaleNm(item.getCodeSaleNm())
                                                          .codeHeatNm(item.getCodeHeatNm())
                                                          .kaptTarea(item.getKaptTarea())
                                                          .kaptDongCnt(item.getKaptDongCnt())
                                                          .kaptdaCnt(item.getKaptdaCnt())
                                                          .kaptBcompany(item.getKaptBcompany())
                                                          .kaptAcompany(item.getKaptAcompany())
                                                          .kaptTel(item.getKaptTel())
                                                          .kaptFax(item.getKaptFax())
                                                          .kaptUrl(item.getKaptUrl())
                                                          .codeAptNm(item.getCodeAptNm())
                                                          .doroJuso(item.getDoroJuso())
                                                          .hoCnt(item.getHoCnt())
                                                          .codeMgrNm(item.getCodeMgrNm())
                                                          .codeHallNm(item.getCodeHallNm())
                                                          .kaptUsedate(item.getKaptUsedate())
                                                          .kaptMarea(item.getKaptMarea())
                                                          .kaptMparea_60(item.getKaptMparea_60())
                                                          .kaptMparea_85(item.getKaptMparea_85())
                                                          .kaptMparea_135(item.getKaptMparea_135())
                                                          .kaptMparea_136(item.getKaptMparea_136())
                                                          .privArea(item.getPrivArea())
                                                          .bjdCode(item.getBjdCode())
                                                          .build()).orElse(null);
            }
        } catch (Exception e) {
        }
        return KaptInfo.builder().kaptCode(kaptCode.getKaptCode()).build();
    }

    @Override
    public List<KaptInfo> writeByKaptInfo(List<KaptInfo> kaptInfoList) {
        return kaptInfoRepository.saveAll(kaptInfoList.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @Override
    public List<BuildingRegister> readAllByBuildingRegister() {
        return buildingRepository.findByRegionCodeIsNotNull().stream().filter(x -> StringUtils.isNotEmpty(x.getLotNumber())).map(x -> {
            List<BuildingRegister> buildingRegisters = Lists.newArrayList();
            List<BuildingRegister> byQuery = buildingRegisterRepository.findByQuery(x.getRegionCode() + "/" + x.getLotNumber());
            if(byQuery.size() > 0) {
                return buildingRegisters;
            }
            String regionCode = x.getRegionCode();
            String sigunguCode = x.getRegionCode().substring(0, 5);
            String dongcode = x.getRegionCode().substring(5, 10);

            String[] lotNumber = x.getLotNumber().replace("산", "").replace("가", "").split("-");
            String bunCode = "0000";
            String jiCode = "0000";
            if (lotNumber.length > 0 && NumberUtils.isDigits(lotNumber[0])) {
                bunCode = String.format("%04d", Integer.valueOf(lotNumber[0]));
            }
            if (lotNumber.length > 1 && NumberUtils.isDigits(lotNumber[1])) {
                jiCode = String.format("%04d", Integer.valueOf(lotNumber[1]));
            }

            BuildingRegisterResponse brRecapTitleInfo;
            int page = 1;
            try {
                while (true) {
                    brRecapTitleInfo = buildingRegisterClient.getBrRecapTitleInfo(serviceKey,
                                                                                  sigunguCode,
                                                                                  dongcode,
                                                                                  bunCode,
                                                                                  jiCode,
                                                                                  page++,
                                                                                  100);
                    if (brRecapTitleInfo.getBody().getItems().getItem().size() == 0) {
                        break;
                    }
                    List<BuildingRegister> result = brRecapTitleInfo.getBody()
                                                                    .getItems()
                                                                    .getItem()
                                                                    .stream()
                                                                    .map(register -> BuildingRegister.builder()
                                                                                                     .query(x.getRegionCode() + "/" + x.getLotNumber())
                                                                                                     .rnum(register.getRnum())
                                                                                                     .platPlc(register.getPlatPlc())
                                                                                                     .sigunguCd(register.getSigunguCd())
                                                                                                     .bjdongCd(register.getBjdongCd())
                                                                                                     .platGbCd(register.getPlatGbCd())
                                                                                                     .bun(register.getBun())
                                                                                                     .ji(register.getJi())
                                                                                                     .mgmBldrgstPk(register.getMgmBldrgstPk())
                                                                                                     .regstrGbCd(register.getRegstrGbCd())
                                                                                                     .regstrGbCdNm(register.getRegstrGbCdNm())
                                                                                                     .regstrKindCd(register.getRegstrKindCd())
                                                                                                     .regstrKindCdNm(register.getRegstrKindCdNm())
                                                                                                     .newOldRegstrGbCd(register.getNewOldRegstrGbCd())
                                                                                                     .newOldRegstrGbCdNm(register.getNewOldRegstrGbCdNm())
                                                                                                     .newPlatPlc(register.getNewPlatPlc())
                                                                                                     .bldNm(register.getBldNm())
                                                                                                     .splotNm(register.getSplotNm())
                                                                                                     .block(register.getBlock())
                                                                                                     .lot(register.getLot())
                                                                                                     .bylotCnt(register.getBylotCnt())
                                                                                                     .naRoadCd(register.getNaRoadCd())
                                                                                                     .naBjdongCd(register.getNaBjdongCd())
                                                                                                     .naUgrndCd(register.getNaUgrndCd())
                                                                                                     .naMainBun(register.getNaMainBun())
                                                                                                     .naSubBun(register.getNaSubBun())
                                                                                                     .platArea(register.getPlatArea())
                                                                                                     .archArea(register.getArchArea())
                                                                                                     .bcRat(register.getBcRat())
                                                                                                     .totArea(register.getTotArea())
                                                                                                     .vlRatEstmTotArea(register.getVlRatEstmTotArea())
                                                                                                     .vlRat(register.getVlRat())
                                                                                                     .mainPurpsCd(register.getMainPurpsCd())
                                                                                                     .mainPurpsCdNm(register.getMainPurpsCdNm())
                                                                                                     .etcPurps(register.getEtcPurps())
                                                                                                     .hhldCnt(register.getHhldCnt())
                                                                                                     .fmlyCnt(register.getFmlyCnt())
                                                                                                     .mainBldCnt(register.getMainBldCnt())
                                                                                                     .atchBldCnt(register.getAtchBldCnt())
                                                                                                     .atchBldArea(register.getAtchBldArea())
                                                                                                     .totPkngCnt(register.getTotPkngCnt())
                                                                                                     .indrMechUtcnt(register.getIndrMechUtcnt())
                                                                                                     .indrMechArea(register.getIndrMechArea())
                                                                                                     .oudrMechUtcnt(register.getOudrMechUtcnt())
                                                                                                     .oudrMechArea(register.getOudrMechArea())
                                                                                                     .indrAutoUtcnt(register.getIndrAutoUtcnt())
                                                                                                     .indrAutoArea(register.getIndrAutoArea())
                                                                                                     .oudrAutoUtcnt(register.getOudrAutoUtcnt())
                                                                                                     .oudrAutoArea(register.getOudrAutoArea())
                                                                                                     .pmsDay(register.getPmsDay())
                                                                                                     .stcnsDay(register.getStcnsDay())
                                                                                                     .useAprDay(register.getUseAprDay())
                                                                                                     .pmsnoYear(register.getPmsnoYear())
                                                                                                     .pmsnoKikCd(register.getPmsnoKikCd())
                                                                                                     .pmsnoKikCdNm(register.getPmsnoKikCdNm())
                                                                                                     .pmsnoGbCd(register.getPmsnoGbCd())
                                                                                                     .pmsnoGbCdNm(register.getPmsnoGbCdNm())
                                                                                                     .hoCnt(register.getHoCnt())
                                                                                                     .engrGrade(register.getEngrGrade())
                                                                                                     .engrRat(register.getEngrRat())
                                                                                                     .engrEpi(register.getEngrEpi())
                                                                                                     .gnBldGrade(register.getGnBldGrade())
                                                                                                     .gnBldCert(register.getGnBldCert())
                                                                                                     .itgBldGrade(register.getItgBldGrade())
                                                                                                     .itgBldCert(register.getItgBldCert())
                                                                                                     .crtnDay(register.getCrtnDay())
                                                                                                     .build())
                                                                    .collect(Collectors.toList());

                    buildingRegisters = Stream.concat(buildingRegisters.stream(), result.stream()).collect(Collectors.toList());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            if (buildingRegisters.size() > 0) {
                log.info("buildingRegisters : {}", buildingRegisters.size());
            }
            return buildingRegisters;
        }).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public BuildingRegister processByBuildingRegister(BuildingRegister buildingRegister) {
        return null;
    }

    @Override
    public List<BuildingRegister> writeByBuildingRegister(List<BuildingRegister> buildingRegisters) {
        return buildingRegisterRepository.saveAll(buildingRegisters);
    }
}
