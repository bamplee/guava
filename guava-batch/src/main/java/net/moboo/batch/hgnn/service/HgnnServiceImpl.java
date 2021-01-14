//package net.moboo.batch.hgnn.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.type.SimpleType;
//import com.google.common.collect.Lists;
//import lombok.extern.slf4j.Slf4j;
//import net.moboo.batch.hgnn.feign.HgnnApiClient;
//import net.moboo.batch.hgnn.feign.HgnnLambdaClient;
//import net.moboo.batch.hgnn.feign.HgnnLambdaRequest;
//import net.moboo.batch.hgnn.repository.ApartmentMatchTable;
//import net.moboo.batch.hgnn.repository.ApartmentMatchTableRepository;
//import net.moboo.batch.hgnn.repository.AptTemp;
//import net.moboo.batch.hgnn.repository.AptTempRepository;
//import net.moboo.batch.hgnn.repository.GuavaBuilding;
//import net.moboo.batch.hgnn.repository.GuavaBuildingArea;
//import net.moboo.batch.hgnn.repository.GuavaBuildingAreaRepository;
//import net.moboo.batch.hgnn.repository.GuavaBuildingFailInfo;
//import net.moboo.batch.hgnn.repository.GuavaBuildingFailInfoRepository;
//import net.moboo.batch.hgnn.repository.GuavaBuildingRepository;
//import net.moboo.batch.hgnn.repository.GuavaRegion;
//import net.moboo.batch.hgnn.repository.GuavaRegionRepository;
//import net.moboo.batch.hgnn.repository.RegionTempRepository;
//import net.moboo.batch.infrastructure.jpa.PbRegionCode;
//import net.moboo.batch.infrastructure.jpa.PbRegionCodeRepository;
//import net.moboo.batch.infrastructure.jpa.RegionRepository;
//import org.apache.commons.lang3.ObjectUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.locationtech.jts.geom.Coordinate;
//import org.locationtech.jts.geom.GeometryFactory;
//import org.locationtech.jts.geom.Point;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StopWatch;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Slf4j
//@Service
//public class HgnnServiceImpl implements HgnnService {
////    private final HgnnApiClient hgnnApiClient;
//    private final RegionRepository regionRepository;
//    private final ObjectMapper objectMapper;
//    private final RegionTempRepository regionTempRepository;
//    private final AptTempRepository aptTempRepository;
//    private final ApartmentMatchTableRepository apartmentMatchTableRepository;
//    private final HgnnLambdaClient hgnnLambdaClient;
//    private final GuavaRegionRepository guavaRegionRepository;
//    private final PbRegionCodeRepository pbRegionCodeRepository;
//    private final GuavaBuildingRepository guavaBuildingRepository;
//    private final GuavaBuildingAreaRepository guavaBuildingAreaRepository;
//    private final GuavaBuildingFailInfoRepository guavaBuildingFailInfoRepository;
//
//    public HgnnServiceImpl(RegionRepository regionRepository,
//                           ObjectMapper objectMapper,
//                           RegionTempRepository regionTempRepository,
//                           AptTempRepository aptTempRepository,
//                           ApartmentMatchTableRepository apartmentMatchTableRepository,
//                           HgnnLambdaClient hgnnLambdaClient,
//                           GuavaRegionRepository guavaRegionRepository,
//                           PbRegionCodeRepository pbRegionCodeRepository,
//                           GuavaBuildingRepository guavaBuildingRepository,
//                           GuavaBuildingAreaRepository guavaBuildingAreaRepository,
//                           GuavaBuildingFailInfoRepository guavaBuildingFailInfoRepository) {
//        this.regionRepository = regionRepository;
//        this.objectMapper = objectMapper;
//        this.regionTempRepository = regionTempRepository;
//        this.aptTempRepository = aptTempRepository;
//        this.apartmentMatchTableRepository = apartmentMatchTableRepository;
//        this.hgnnLambdaClient = hgnnLambdaClient;
//        this.guavaRegionRepository = guavaRegionRepository;
//        this.pbRegionCodeRepository = pbRegionCodeRepository;
//        this.guavaBuildingRepository = guavaBuildingRepository;
//        this.guavaBuildingAreaRepository = guavaBuildingAreaRepository;
//        this.guavaBuildingFailInfoRepository = guavaBuildingFailInfoRepository;
//    }
//
//    @Override
//    public List<ApartmentMatchTable> getAllApartmentMatchTableList() {
//        return apartmentMatchTableRepository.findByHgnnIdIsNull();
//    }
//
//    @Override
//    public ApartmentMatchTable transform(ApartmentMatchTable apartmentMatchTable) {
//        if (StringUtils.isEmpty(apartmentMatchTable.getHgnnId())) {
//            String regionCode = apartmentMatchTable.getDongSigunguCode() + apartmentMatchTable.getDongCode().substring(0, 3) + "00";
//            List<AptTemp> byRegionCode = aptTempRepository.findByRegionCode(regionCode);
//            byRegionCode = byRegionCode.stream().filter(AptTemp::getIsValid).collect(Collectors.toList());
//            for (AptTemp aptTemp : byRegionCode) {
//                JsonNode jsonNode = null;
//                try {
//                    jsonNode = objectMapper.readValue(aptTemp.getData(), SimpleType.constructUnsafe(JsonNode.class));
//                    String[] split = jsonNode.get("result").get("data").get("address").toString().replace("\"", "").split(" ");
//                    String lotNumber = split[split.length - 1];
//                    if (apartmentMatchTable.getLotNumber().equals(lotNumber)) {
//                        String name = jsonNode.get("result").get("data").get("name").toString().replace("\"", "");
//                        String portalId = null;
//                        try {
//                            portalId = jsonNode.get("result").get("data").get("portal_id").toString().replace("\"", "");
//                        } catch (Exception e) {
//                        }
//                        apartmentMatchTable.setHgnnId(aptTemp.getAptId());
//                        apartmentMatchTable.setHgnnAptName(name);
//                        apartmentMatchTable.setHgnnRegionCode(aptTemp.getRegionCode());
//                        apartmentMatchTable.setPortalId(portalId);
//                        return apartmentMatchTable;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return apartmentMatchTable;
//    }
//
//    @Override
//    public List<ApartmentMatchTable> setApartmentMatchTables(List<ApartmentMatchTable> apartmentMatchTables) {
//        return apartmentMatchTableRepository.saveAll(apartmentMatchTables);
//    }
//
//    @Override
//    public void test() {
//        List<ApartmentMatchTable> findAll = apartmentMatchTableRepository.findAll();
//        for (int i = 0; i < findAll.size(); i++) {
//            ApartmentMatchTable amt = findAll.get(i);
//            if (StringUtils.isEmpty(amt.getHgnnId())) {
//                String regionCode = amt.getDongSigunguCode() + amt.getDongCode().substring(0, 3) + "00";
//                List<AptTemp> byRegionCode = aptTempRepository.findByRegionCode(regionCode);
//                byRegionCode = byRegionCode.stream().filter(AptTemp::getIsValid).collect(Collectors.toList());
//                for (AptTemp aptTemp : byRegionCode) {
//                    JsonNode jsonNode = null;
//                    try {
//                        jsonNode = objectMapper.readValue(aptTemp.getData(), SimpleType.constructUnsafe(JsonNode.class));
//                        String[] split = jsonNode.get("result").get("data").get("address").toString().replace("\"", "").split(" ");
//                        String lotNumber = split[split.length - 1];
//                        if (amt.getLotNumber().equals(lotNumber)) {
//                            String name = jsonNode.get("result").get("data").get("name").toString().replace("\"", "");
//                            String portalId = null;
//                            try {
//                                portalId = jsonNode.get("result").get("data").get("portal_id").toString().replace("\"", "");
//                            } catch (Exception e) {
//                            }
//                            amt.setHgnnId(aptTemp.getAptId());
//                            amt.setHgnnAptName(name);
//                            amt.setHgnnRegionCode(aptTemp.getRegionCode());
//                            amt.setPortalId(portalId);
//                            apartmentMatchTableRepository.save(amt);
//                            System.out.println(findAll.size() + "/" + i);
//                            break;
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void fetchHgnnRegion() {
//        String fileName = "region.txt";
//        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//
//        File file = new File(classLoader.getResource(fileName).getFile());
//
//        System.out.println("File Found : " + file.exists());
//        String[] content = new String[0];
//        try {
//            content = new String(Files.readAllBytes(file.toPath())).split("\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        List<String> regionList = Lists.newArrayList(content);
//        for (int i = 0; i < regionList.size(); i++) {
//            System.out.println(regionList.size() + "/" + i);
//            String x = regionList.get(i);
//            this.executor(x);
//        }
//        System.out.println(" === END");
//    }
//
//    @Override
//    public List<AptTemp> read() {
//        return Lists.newArrayList();
//    }
//
//    @Override
//    public List<GuavaRegion> readBuilding() {
//        return guavaRegionRepository.findAll();
//    }
//
//    //    @Async("executorSample")
//    private GuavaHgnnBuilding executor(String query) {
//        try {
//            StopWatch stopWatch = new StopWatch();
//            stopWatch.start(query);
//            GuavaHgnnBuilding data = hgnnLambdaClient.drawTest(HgnnLambdaRequest.builder()
//                                                                                .query(query)
//                                                                                .build()).getResult().getData();
//            stopWatch.stop();
////            log.info("time : {} | query : {}", stopWatch.getTotalTimeSeconds(), query);
//            return data;
//        } catch (Exception e) {
////            log.error("query : {}, message : {}", e.getMessage());
//            GuavaBuildingFailInfo guavaBuildingFailInfo = new GuavaBuildingFailInfo();
//            guavaBuildingFailInfo.setBuildingCode(query);
//            guavaBuildingFailInfoRepository.save(guavaBuildingFailInfo);
//        }
//        return null;
//    }
//
//    @Override
//    public GuavaRegion process(PbRegionCode pbRegionCode) {
////        try {
////            GuavaHgnnResponse<GuavaHgnnRegion> data = hgnnApiClient.getGuavaHgnnRegion(String.valueOf(pbRegionCode.getCode()));
////            GuavaRegion guavaRegion = new GuavaRegion();
//////        try {
////////            JsonNode jsonNode = this.objectMapper.readValue(data.getData(), JsonNode.class);
//////            System.out.println();
//////        } catch (JsonProcessingException e) {
//////            e.printStackTrace();
//////        }
////            GuavaHgnnRegion regionData = data.getData();
////            String closeRegionIdCsv = "";
////            if (StringUtils.isNotEmpty(regionData.getCloseRegion())) {
////                try {
////                    JsonNode jsonNode = objectMapper.readValue(regionData.getCloseRegion(), JsonNode.class);
////                    List<String> closeRegionList = Lists.newArrayList();
////                    if (jsonNode != null & jsonNode.isArray()) {
////                        for (JsonNode node : jsonNode) {
////                            closeRegionList.add(node.textValue());
////                        }
////                        closeRegionIdCsv = StringUtils.join(closeRegionList, ",");
////                    }
////                } catch (JsonProcessingException e) {
////                    e.printStackTrace();
////                }
////            }
////
////            String buildingIdCsv = StringUtils.join(Stream.concat(regionData.getApts().stream(), regionData.getOfficetels().stream())
////                                                          .map(GuavaHgnnBuilding::getId)
////                                                          .collect(Collectors.toList()), ",");
////
////            return GuavaRegion.builder()
////                              .regionCode(regionData.getId())
////                              .name(regionData.getName())
////                              .sido(regionData.getSido())
////                              .sidoName(regionData.getSidoName())
////                              .sigungu(regionData.getSigungu())
////                              .sigunguName(regionData.getSigunguName())
////                              .dong(regionData.getDong())
////                              .dongName(regionData.getDongName())
////                              .ri(regionData.getRi())
////                              .riName(regionData.getRiName())
////                              .lat(regionData.getLat())
////                              .lng(regionData.getLng())
////                              .point(this.createPoint(regionData.getLng(), regionData.getLat()))
////                              .closeRegionIdCsv(closeRegionIdCsv)
////                              .buildingIdCsv(buildingIdCsv)
////                              .build();
////        } catch (Exception e) {
////            return null;
////        }
//        return null;
//    }
//
//    @Override
//    public GuavaBuilding process(GuavaRegion data) {
////        GuavaHgnnBaseInfo baseinfo = data.getBaseinfo();
////        GuavaBuilding build = GuavaBuilding.builder()
////                                           .buildingCode(data.getId())
////                                           .type(data.getType())
////                                           .adminRegionCode(data.getAdminRegionCode())
////                                           .regionCode(data.getRegionCode())
////                                           .originalName("")
////                                           .name(data.getName())
////                                           .address(data.getAddress())
////                                           .roadAddress(data.getRoadAddress())
////                                           .portalId(data.getPortalId())
////                                           .totalHousehold(data.getTotalHousehold())
////
//////                                               .manageCostYear(baseinfo.ifPresent(x -> x.ge_))
//////                                               .manageCostSummer()
//////                                               .manageCostWinter()
////                                           .floorAreaRatio(baseinfo != null && baseinfo.getFloorAreaRatio() != null ?
////                                                               baseinfo.getFloorAreaRatio() : 0)
////                                           .buildingCoverageRatio(baseinfo != null && baseinfo.getBuildingCoverageRatio() != null ? baseinfo
////                                               .getBuildingCoverageRatio() : 0)
////                                           .buildingCount(baseinfo != null && baseinfo.getBuildingCount() != null ?
////                                                              baseinfo.getBuildingCount() : 0)
////                                           .floorMax(baseinfo != null && baseinfo.getFloorMax() != null ? baseinfo.getFloorMax()
////                                                         : 0)
////                                           .floorMin(baseinfo != null && baseinfo.getFloorMin() != null ? baseinfo.getFloorMin()
////                                                         : 0)
////                                           .tel(baseinfo != null && baseinfo.getTel() != null ? baseinfo.getTel() : "")
////                                           .parkingTotal(baseinfo != null && baseinfo.getParkingTotal() != null ?
////                                                             baseinfo.getParkingTotal() : 0)
////                                           .parkingInside(baseinfo != null && baseinfo.getParkingInside() != null ?
////                                                              baseinfo.getParkingInside() : 0)
////                                           .parkingOutside(baseinfo != null && baseinfo.getParkingOutside() != null ?
////                                                               baseinfo.getParkingOutside() : 0)
////                                           .parkingRate(baseinfo != null && baseinfo.getParkingRate() != null ?
////                                                            baseinfo.getParkingRate() : 0)
////                                           .approvalDate(baseinfo != null && baseinfo.getApprovalDate() != null ?
////                                                             baseinfo.getApprovalDate() : "")
////                                           .baseInfoId(baseinfo != null && baseinfo.getId() != null ? baseinfo.getId() : "")
////                                           .totalHousehold(baseinfo != null && baseinfo.getTotalHousehold() != null ?
////                                                               baseinfo.getTotalHousehold() : 0)
////                                           .totalHouseholdCounted(baseinfo != null && baseinfo.getTotalHouseholdCounted() != null ? baseinfo
////                                               .getTotalHouseholdCounted() : 0)
////                                           .company(baseinfo != null && baseinfo.getCompany() != null ? baseinfo.getCompany() : "")
////                                           .asileType(baseinfo != null && baseinfo.getAsileType() != null ?
////                                                          baseinfo.getAsileType() : "")
////                                           .heatType(baseinfo != null && baseinfo.getHeatType() != null ? baseinfo.getHeatType()
////                                                         : "")
////                                           .heatSource(baseinfo != null && baseinfo.getHeatSource() != null ?
////                                                           baseinfo.getHeatSource() : "")
////
////                                           .rentalBusinessRatio(data.getRentalBusinessRatio())
////                                           .totalRentalBusinessHousehold(data.getTotalRentalBusinessHousehold())
////                                           .businessCallPrice(data.getBusinessCallPrice())
////                                           .startMonth(data.getStartMonth())
////                                           .lat(data.getLat())
////                                           .lng(data.getLng())
////                                           .point(this.createPoint(data.getLng(), data.getLat()))
////                                           .roadviewLat(data.getRoadviewLat())
////                                           .roadviewLng(data.getRoadviewLng())
////                                           .diffYearText(data.getDiffYearText())
////                                           .diffYearShortText(data.getDiffYearShortText())
////                                           .dong(data.getDong())
//////                                               .moliteId(data.getId())
//////                                               .moliteCategory()
////                                           .build();
////        List<GuavaHgnnBuilding.Area> areaList = data.getArea();
////        if (areaList != null) {
////            for (GuavaHgnnBuilding.Area buildingArea : areaList) {
////                GuavaBuildingArea guavaBuildingArea = new GuavaBuildingArea();
////                guavaBuildingArea.setBuildingCode(build.getBuildingCode());
////                guavaBuildingArea.setPrivateArea(buildingArea.getPrivateArea());
////                guavaBuildingArea.setPublicArea(buildingArea.getPublicArea());
////                guavaBuildingArea.setTotalHousehold(buildingArea.getTotalHousehold());
////                guavaBuildingArea.setAreaType(buildingArea.getAreaType());
////                build.addArea(guavaBuildingArea);
////            }
////        }
////        log.info("build : {}", build.getBuildingCode());
////        return build;
//        return GuavaBuilding.builder().build();
//    }
//
//    private GuavaBuilding transform(GuavaHgnnBuilding guavaHgnnBuilding) {
//        GuavaHgnnBaseInfo baseinfo = guavaHgnnBuilding.getBaseinfo();
//        GuavaBuilding build = GuavaBuilding.builder()
//                                           .buildingCode(guavaHgnnBuilding.getId())
//                                           .type(guavaHgnnBuilding.getType())
//                                           .adminRegionCode(guavaHgnnBuilding.getAdminRegionCode())
//                                           .regionCode(guavaHgnnBuilding.getRegionCode())
//                                           .originalName("")
//                                           .name(guavaHgnnBuilding.getName())
//                                           .address(guavaHgnnBuilding.getAddress())
//                                           .roadAddress(guavaHgnnBuilding.getRoadAddress())
//                                           .portalId(guavaHgnnBuilding.getPortalId())
//                                           .totalHousehold(guavaHgnnBuilding.getTotalHousehold())
//
////                                               .manageCostYear(baseinfo.ifPresent(x -> x.ge_))
////                                               .manageCostSummer()
////                                               .manageCostWinter()
//                                           .floorAreaRatio(baseinfo != null && baseinfo.getFloorAreaRatio() != null ?
//                                                               baseinfo.getFloorAreaRatio() : 0)
//                                           .buildingCoverageRatio(baseinfo != null && baseinfo.getBuildingCoverageRatio() != null ? baseinfo
//                                               .getBuildingCoverageRatio() : 0)
//                                           .buildingCount(baseinfo != null && baseinfo.getBuildingCount() != null ?
//                                                              baseinfo.getBuildingCount() : 0)
//                                           .floorMax(baseinfo != null && baseinfo.getFloorMax() != null ? baseinfo.getFloorMax()
//                                                         : 0)
//                                           .floorMin(baseinfo != null && baseinfo.getFloorMin() != null ? baseinfo.getFloorMin()
//                                                         : 0)
//                                           .tel(baseinfo != null && baseinfo.getTel() != null ? baseinfo.getTel() : "")
//                                           .parkingTotal(baseinfo != null && baseinfo.getParkingTotal() != null ?
//                                                             baseinfo.getParkingTotal() : 0)
//                                           .parkingInside(baseinfo != null && baseinfo.getParkingInside() != null ?
//                                                              baseinfo.getParkingInside() : 0)
//                                           .parkingOutside(baseinfo != null && baseinfo.getParkingOutside() != null ?
//                                                               baseinfo.getParkingOutside() : 0)
//                                           .parkingRate(baseinfo != null && baseinfo.getParkingRate() != null ?
//                                                            baseinfo.getParkingRate() : 0)
//                                           .approvalDate(baseinfo != null && baseinfo.getApprovalDate() != null ?
//                                                             baseinfo.getApprovalDate() : "")
//                                           .baseInfoId(baseinfo != null && baseinfo.getId() != null ? baseinfo.getId() : "")
//                                           .totalHousehold(baseinfo != null && baseinfo.getTotalHousehold() != null ?
//                                                               baseinfo.getTotalHousehold() : 0)
//                                           .totalHouseholdCounted(baseinfo != null && baseinfo.getTotalHouseholdCounted() != null ? baseinfo
//                                               .getTotalHouseholdCounted() : 0)
//                                           .company(baseinfo != null && baseinfo.getCompany() != null ? baseinfo.getCompany() : "")
//                                           .asileType(baseinfo != null && baseinfo.getAsileType() != null ?
//                                                          baseinfo.getAsileType() : "")
//                                           .heatType(baseinfo != null && baseinfo.getHeatType() != null ? baseinfo.getHeatType()
//                                                         : "")
//                                           .heatSource(baseinfo != null && baseinfo.getHeatSource() != null ?
//                                                           baseinfo.getHeatSource() : "")
//
//                                           .rentalBusinessRatio(guavaHgnnBuilding.getRentalBusinessRatio())
//                                           .totalRentalBusinessHousehold(guavaHgnnBuilding.getTotalRentalBusinessHousehold())
//                                           .businessCallPrice(guavaHgnnBuilding.getBusinessCallPrice())
//                                           .startMonth(guavaHgnnBuilding.getStartMonth())
//                                           .lat(guavaHgnnBuilding.getLat())
//                                           .lng(guavaHgnnBuilding.getLng())
//                                           .point(this.createPoint(guavaHgnnBuilding.getLng(), guavaHgnnBuilding.getLat()))
//                                           .roadviewLat(guavaHgnnBuilding.getRoadviewLat())
//                                           .roadviewLng(guavaHgnnBuilding.getRoadviewLng())
//                                           .diffYearText(guavaHgnnBuilding.getDiffYearText())
//                                           .diffYearShortText(guavaHgnnBuilding.getDiffYearShortText())
//                                           .dong(guavaHgnnBuilding.getDong())
////                                               .moliteId(data.getId())
////                                               .moliteCategory()
//                                           .build();
//        List<GuavaHgnnBuilding.Area> areaList = guavaHgnnBuilding.getArea();
//        if (areaList != null) {
//            for (GuavaHgnnBuilding.Area buildingArea : areaList) {
//                GuavaBuildingArea guavaBuildingArea = new GuavaBuildingArea();
//                guavaBuildingArea.setBuildingCode(build.getBuildingCode());
//                guavaBuildingArea.setPrivateArea(buildingArea.getPrivateArea());
//                guavaBuildingArea.setPublicArea(buildingArea.getPublicArea());
//                guavaBuildingArea.setTotalHousehold(buildingArea.getTotalHousehold());
//                guavaBuildingArea.setAreaType(buildingArea.getAreaType());
//                build.addArea(guavaBuildingArea);
//            }
//        }
////        log.info("build : {}", build.getBuildingCode());
//        return build;
//    }
//
//    @Override
//    public List<GuavaRegion> write(List<GuavaRegion> guavaRegions) {
//        return guavaRegionRepository.saveAll(guavaRegions);
//    }
//
//    @Override
//    public List<GuavaBuilding> writeBuilding(List<GuavaBuilding> guavaRegions) {
//        log.info("save size : {}", guavaRegions.size());
//
//        List<GuavaRegion> guavaRegionList = guavaRegionRepository.findAll();
//        List<String> buildingList = guavaRegionList.stream()
//                                                   .map(x -> Lists.newArrayList(x.getBuildingIdCsv().split(",")))
//                                                   .flatMap(Collection::stream)
//                                                   .filter(StringUtils::isNotEmpty)
//                                                   .collect(Collectors.toList());
//        log.info("building size : {}", buildingList.size());
//        List<String> existBuildingCodes = guavaBuildingRepository.findAll().stream().map(GuavaBuilding::getBuildingCode).collect(Collectors.toList());
//        List<List<String>> buildingPartitionList = Lists.partition(buildingList.stream().filter(x -> !existBuildingCodes.contains(x)).collect(Collectors.toList()), 500);
//        for (int i = 0; i < buildingPartitionList.size(); i++) {
//            log.info("{} / {}", i, buildingPartitionList.size());
//            List<String> strings = buildingPartitionList.get(i);
//            List<GuavaBuilding> collect = strings.parallelStream()
//                                                 .map(this::executor)
//                                                 .filter(ObjectUtils::isNotEmpty)
//                                                 .map(this::transform)
//                                                 .collect(Collectors.toList());
//            List<GuavaBuilding> guavaBuildings1 = guavaBuildingRepository.saveAll(collect);
////            log.info("success, {}", guavaBuildings1.size());
//        }
//        return Lists.newArrayList();//guavaBuildingRepository.saveAll(guavaRegions);
//    }
//
//    @Override
//    public void sync() {
//        List<GuavaRegion> guavaRegionList = guavaRegionRepository.findAll();
//        List<String> buildingList = guavaRegionList.stream()
//                                                   .map(x -> Lists.newArrayList(x.getBuildingIdCsv().split(",")))
//                                                   .flatMap(Collection::stream)
//                                                   .filter(StringUtils::isNotEmpty)
//                                                   .collect(Collectors.toList());
//        log.info("building size : {}", buildingList.size());
//        List<String> existBuildingCodes = guavaBuildingRepository.findAll().stream().map(GuavaBuilding::getBuildingCode).collect(Collectors.toList());
//        List<List<String>> buildingPartitionList = Lists.partition(buildingList.stream().filter(x -> !existBuildingCodes.contains(x)).collect(Collectors.toList()), 10);
//        for (int i = 0; i < buildingPartitionList.size(); i++) {
//            log.info("{} / {}", i, buildingPartitionList.size());
//            List<String> strings = buildingPartitionList.get(i);
//            List<GuavaBuilding> collect = strings.parallelStream()
//                                                 .map(this::executor)
//                                                 .filter(ObjectUtils::isNotEmpty)
//                                                 .map(this::transform)
//                                                 .collect(Collectors.toList());
//            List<GuavaBuilding> guavaBuildings1 = guavaBuildingRepository.saveAll(collect);
////            log.info("success, {}", guavaBuildings1.size());
//        }
//    }
//
//    private Point createPoint(double lng, double lat) {
//        GeometryFactory gf = new GeometryFactory();
//        return gf.createPoint(new Coordinate(lng, lat));
//    }
//}
