package net.moboo.batch.wooa.service;

import net.moboo.batch.wooa.datatool.KaptCodeResponse;
import net.moboo.batch.wooa.repository.BuildingRegister;
import net.moboo.batch.wooa.repository.KaptCode;
import net.moboo.batch.wooa.repository.KaptInfo;

import java.util.List;

public interface KaptService {
    List<KaptCode> read(String regionCode);

    List<KaptCode> readAll();

    KaptCode process(KaptCode kaptCodeResponse);

    List<KaptCode> write(List<KaptCode> kaptCodeList);

    List<KaptCode> readAllByKaptInfo();

    KaptInfo processByKaptInfo(KaptCode kaptCode);

    List<KaptInfo> writeByKaptInfo(List<KaptInfo> kaptInfoList);

    List<BuildingRegister> readAllByBuildingRegister();

    BuildingRegister processByBuildingRegister(BuildingRegister buildingRegister);

    List<BuildingRegister> writeByBuildingRegister(List<BuildingRegister> buildingRegisters);
}
