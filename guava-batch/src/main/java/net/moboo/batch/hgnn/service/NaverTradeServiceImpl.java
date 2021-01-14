package net.moboo.batch.hgnn.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.domain.TradeArticle;
import net.moboo.batch.domain.TradeItem;
import net.moboo.batch.hgnn.feign.NaverLambdaClient;
import net.moboo.batch.hgnn.feign.NaverPcLandResponse;
import net.moboo.batch.hgnn.repository.ApartmentMatchTableRepository;
import net.moboo.batch.hgnn.repository.GuavaBuilding;
import net.moboo.batch.hgnn.repository.GuavaBuildingRepository;
import net.moboo.batch.hgnn.repository.GuavaMappingInfo;
import net.moboo.batch.hgnn.repository.GuavaMappingInfoRepository;
import net.moboo.batch.infrastructure.jpa.TradeArticleRepository;
import net.moboo.batch.hgnn.feign.NaverLandClient;
import net.moboo.batch.hgnn.feign.NaverLandResponse;
import net.moboo.batch.infrastructure.jpa.TradeItemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class NaverTradeServiceImpl implements NaverTradeService {
    private final ApartmentMatchTableRepository apartmentMatchTableRepository;
    private final NaverLandClient naverLandClient;
    private final NaverLambdaClient naverLambdaClient;
    private final TradeItemRepository tradeItemRepository;
    private final TradeArticleRepository tradeArticleRepository;
    private final GuavaBuildingRepository guavaBuildingRepository;
    private final GuavaMappingInfoRepository guavaMappingInfoRepository;

    public NaverTradeServiceImpl(ApartmentMatchTableRepository apartmentMatchTableRepository,
                                 NaverLandClient naverLandClient,
                                 NaverLambdaClient naverLambdaClient,
                                 TradeItemRepository tradeItemRepository,
                                 TradeArticleRepository tradeArticleRepository,
                                 GuavaBuildingRepository guavaBuildingRepository,
                                 GuavaMappingInfoRepository guavaMappingInfoRepository) {
        this.apartmentMatchTableRepository = apartmentMatchTableRepository;
        this.naverLandClient = naverLandClient;
        this.naverLambdaClient = naverLambdaClient;
        this.tradeItemRepository = tradeItemRepository;
        this.tradeArticleRepository = tradeArticleRepository;
        this.guavaBuildingRepository = guavaBuildingRepository;
        this.guavaMappingInfoRepository = guavaMappingInfoRepository;
    }

    @Override
    public List<TradeArticle> read(String regionCodes) {
        List<String> regionCodeList = Lists.newArrayList(regionCodes.split(","));
        List<TradeArticle> data = Lists.newArrayList();
        for (String parentRegionCode : regionCodeList) {
            List<GuavaMappingInfo> uniqueList = guavaMappingInfoRepository.findByRegionCodeStartsWithAndTypeAndPortalIdIsNotNull(
                parentRegionCode,
                0);
            List<TradeArticle> beforeList = tradeArticleRepository.findByRegionCodeStartsWithAndEndDateIsNull(parentRegionCode);
            List<TradeArticle> svcList = uniqueList.stream()
                                                   .map(x -> {
                                                       int page = 1;
                                                       String portalId = String.valueOf(x.getPortalId());
                                                       List<TradeArticle> result = Lists.newArrayList();
                                                       try {
                                                           Thread.sleep(500);
                                                           NaverPcLandResponse complexArticleList =
                                                               naverLambdaClient.getLandDetail(portalId,
                                                                                                                                    page++)
                                                                                                                     .getData();
//                                                           NaverPcLandResponse complexArticleList = naverLandClient.getPcData(portalId,
//                                                                                                                              "",
//                                                                                                                              page++,
//                                                                                                                              "true");
                                                           result.addAll(complexArticleList.getArticleList()
                                                                                           .stream()
                                                                                           .map(y -> this.transform(portalId, x, y))
                                                                                           .collect(Collectors.toList()));

                                                           while (complexArticleList.getIsMoreData()) {
                                                               Thread.sleep(500);
                                                               complexArticleList = naverLambdaClient.getLandDetail(portalId, page++)
                                                                                                     .getData();
//                                                               complexArticleList = naverLandClient.getPcData(portalId,
//                                                                                                              "",
//                                                                                                              page++,
//                                                                                                              "true");
                                                               result.addAll(complexArticleList.getArticleList()
                                                                                               .stream()
                                                                                               .map(y -> this.transform(portalId, x, y))
                                                                                               .collect(Collectors.toList()));
                                                           }
                                                       } catch (Exception e) {
                                                           log.error(e.getMessage());
                                                       }
                                                       log.info("portalId : {}, size : {}", portalId, result.size());
                                                       return result;
                                                   }).flatMap(Collection::stream).collect(Collectors.toList());
            List<String> beforeAtclNoList = beforeList.stream().map(TradeArticle::getArticleNo).collect(Collectors.toList());
            List<String> svcAtclNoList = svcList.stream().map(TradeArticle::getArticleNo).collect(Collectors.toList());

            // 종료처리
            List<TradeArticle> beforeResult = beforeList.stream()
                                                        .filter(x -> !svcAtclNoList.contains(x.getArticleNo()))
                                                        .peek(x -> x.setEndDate(LocalDateTime.now()
                                                                                             .format(DateTimeFormatter.ofPattern(
                                                                                                 "yyyyMMdd"))))
                                                        .collect(Collectors.toList());
            // 신규 리스트
            List<TradeArticle> afterResult = svcList.stream()
                                                    .filter(x -> !beforeAtclNoList.contains(x.getArticleNo()))
                                                    .collect(Collectors.toList());
            log.info("end : {}, svc : {}, after : {}", beforeResult.size(), beforeAtclNoList.size(), afterResult.size());
            data.addAll(Stream.concat(beforeResult.stream(), afterResult.stream()).collect(Collectors.toList()));
        }
        return data;
    }

    private TradeItem transform(String hcpcNo, NaverLandResponse.Result.Item item) {
        TradeItem tradeItem = new TradeItem();
        tradeItem.setHcpcNo(hcpcNo);
        tradeItem.setType("NAVER");
        tradeItem.setStartDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        tradeItem.setAtclFetrDesc(item.getAtclFetrDesc());
        tradeItem.setAtclNm(item.getAtclNm());
        tradeItem.setAtclNo(item.getAtclNo());
        tradeItem.setAtclStatCd(item.getAtclStatCd());
        tradeItem.setBildNm(item.getBildNm());
        tradeItem.setCfmYmd(item.getCfmYmd());
        tradeItem.setCpCnt(item.getCpCnt());
        tradeItem.setCpNm(item.getCpNm());
        tradeItem.setCpid(item.getCpid());
        tradeItem.setDirectTradYn(item.getDirectTradYn());
        tradeItem.setDirection(item.getDirection());
        tradeItem.setDtlAddr(item.getDtlAddr());
        tradeItem.setDtlAddrYn(item.getDtlAddrYn());
        tradeItem.setFlrInfo(item.getFlrInfo());
        tradeItem.setPrcInfo(item.getPrcInfo());
        tradeItem.setRepImgThumb(item.getRepImgThumb());
        tradeItem.setRepImgTpCd(item.getRepImgTpCd());
        tradeItem.setRepImgUrl(item.getRepImgUrl());
        tradeItem.setRletTpCd(item.getRletTpCd());
        tradeItem.setRletTpNm(item.getRletTpNm());
        tradeItem.setRltrNm(item.getRltrNm());
        tradeItem.setSameAddrCnt(item.getSameAddrCnt());
        tradeItem.setSameAddrDirectCnt(item.getSameAddrDirectCnt());
        tradeItem.setSameAddrHash(item.getSameAddrHash());
        tradeItem.setSameAddrMaxPrc(item.getSameAddrMaxPrc());
        tradeItem.setSameAddrMinPrc(item.getSameAddrMinPrc());
        tradeItem.setSpc1(item.getSpc1());
        tradeItem.setSpc2(item.getSpc2());
        tradeItem.setTagList(StringUtils.join(item.getTagList(), ","));
        tradeItem.setTradCmplYn(item.getTradCmplYn());
        tradeItem.setTradTpCd(item.getTradTpCd());
        tradeItem.setTradTpNm(item.getTradTpNm());
        tradeItem.setTradeCheckedByOwner(item.getTradeCheckedByOwner());
        tradeItem.setTradePriceHan(item.getTradePriceHan());
        tradeItem.setTradePriceInfo(item.getTradePriceInfo());
        tradeItem.setVrfcTpCd(item.getVrfcTpCd());
        tradeItem.setSellrNm(item.getSellrNm());
        if (item.getCpLinkVO() != null) {
            NaverLandResponse.Result.Item.CpLinkVO cpLinkVO = item.getCpLinkVO();
            tradeItem.setMobileArticleUrl(cpLinkVO.getMobileArticleUrl());
            tradeItem.setMobileArticleLinkTypeCode(cpLinkVO.getMobileArticleLinkTypeCode());
            tradeItem.setMobileArticleLinkUseAtArticleTitle(cpLinkVO.getMobileArticleLinkUseAtArticleTitle());
            tradeItem.setMobileArticleLinkUseAtCpName(cpLinkVO.getMobileArticleLinkUseAtCpName());
            tradeItem.setMobileBmsInspectPassYn(cpLinkVO.getMobileBmsInspectPassYn());
            tradeItem.setPcArticleLinkUseAtArticleTitle(cpLinkVO.getPcArticleLinkUseAtArticleTitle());
            tradeItem.setPcArticleLinkUseAtCpName(cpLinkVO.getPcArticleLinkUseAtCpName());
        }
        return tradeItem;
    }

    private TradeArticle transform(String hcpcNo, GuavaMappingInfo guavaMappingInfo, NaverPcLandResponse.Article item) {
        TradeArticle tradeArticle = new TradeArticle();
        tradeArticle.setPortalId(hcpcNo);
        tradeArticle.setRegionCode(guavaMappingInfo.getRegionCode());
        tradeArticle.setBuildingCode(guavaMappingInfo.getBuildingCode());
        tradeArticle.setType("NAVER");
        tradeArticle.setBaseDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        tradeArticle.setStartDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        tradeArticle.setEndDate(null);
        tradeArticle.setArticleNo(item.getArticleNo());
        tradeArticle.setArticleName(item.getArticleName());
        tradeArticle.setArticleStatus(item.getArticleStatus());
        tradeArticle.setRealEstateTypeCode(item.getRealEstateTypeCode());
        tradeArticle.setRealEstateTypeName(item.getRealEstateTypeName());
        tradeArticle.setArticleRealEstateTypeCode(item.getArticleRealEstateTypeCode());
        tradeArticle.setArticleRealEstateTypeName(item.getArticleRealEstateTypeName());
        tradeArticle.setTradeTypeCode(item.getTradeTypeCode());
        tradeArticle.setTradeTypeName(item.getTradeTypeName());
        tradeArticle.setVerificationTypeCode(item.getVerificationTypeCode());
        tradeArticle.setFloorInfo(item.getFloorInfo());
        tradeArticle.setPriceChangeState(item.getPriceChangeState());
        tradeArticle.setIsPriceModification(item.getIsPriceModification());
        tradeArticle.setDealOrWarrantPrc(item.getDealOrWarrantPrc());
        tradeArticle.setAreaName(item.getAreaName());
        tradeArticle.setArea1(item.getArea1());
        tradeArticle.setArea2(item.getArea2());
        tradeArticle.setDirection(item.getDirection());
        tradeArticle.setArticleConfirmYmd(item.getArticleConfirmYmd());
        tradeArticle.setSiteImageCount(item.getSiteImageCount());
        tradeArticle.setArticleFeatureDesc(item.getArticleFeatureDesc());
        tradeArticle.setTagList(StringUtils.join(item.getTagList(), ","));
        tradeArticle.setBuildingName(item.getBuildingName());
        tradeArticle.setSameAddrCnt(item.getSameAddrCnt());
        tradeArticle.setSameAddrDirectCnt(item.getSameAddrDirectCnt());
        tradeArticle.setSameAddrMaxPrc(item.getSameAddrMaxPrc());
        tradeArticle.setSameAddrMinPrc(item.getSameAddrMinPrc());
        tradeArticle.setPremiumPrc(item.getPremiumPrc());
        tradeArticle.setCpid(item.getCpid());
        tradeArticle.setCpName(item.getCpName());
        tradeArticle.setRepresentativeImgThumb(item.getRepresentativeImgThumb());
        tradeArticle.setRepresentativeImgTypeCode(item.getRepresentativeImgTypeCode());
        tradeArticle.setRepresentativeImgUrl(item.getRepresentativeImgUrl());
        tradeArticle.setCpPcArticleUrl(item.getCpPcArticleUrl());
        tradeArticle.setCpPcArticleBridgeUrl(item.getCpPcArticleBridgeUrl());
        tradeArticle.setCpPcArticleLinkUseAtArticleTitleYn(item.getCpPcArticleLinkUseAtArticleTitleYn());
        tradeArticle.setCpPcArticleLinkUseAtCpNameYn(item.getCpPcArticleLinkUseAtCpNameYn());
        tradeArticle.setCpMobileArticleUrl(item.getCpMobileArticleUrl());
        tradeArticle.setCpMobileArticleLinkUseAtArticleTitleYn(item.getCpMobileArticleLinkUseAtArticleTitleYn());
        tradeArticle.setCpMobileArticleLinkUseAtCpNameYn(item.getCpMobileArticleLinkUseAtCpNameYn());
        tradeArticle.setLatitude(item.getLatitude());
        tradeArticle.setLongitude(item.getLongitude());
        tradeArticle.setIsLocationShow(item.getIsLocationShow());
        tradeArticle.setRealtorName(item.getRealtorName());
        tradeArticle.setRealtorId(item.getRealtorId());
        tradeArticle.setTradeCheckedByOwner(item.getTradeCheckedByOwner());
        tradeArticle.setIsDirectTrade(item.getIsDirectTrade());
        tradeArticle.setIsInterest(item.getIsInterest());
        tradeArticle.setIsComplex(item.getIsComplex());
        tradeArticle.setDetailAddress(item.getDetailAddress());
        tradeArticle.setDetailAddressYn(item.getDetailAddressYn());
        tradeArticle.setSellerName(item.getSellerName());
        tradeArticle.setSellerPhoneNum(item.getSellerPhoneNum());
        tradeArticle.setTradeDepositPrice(item.getTradeDepositPrice());
        tradeArticle.setTradeRentPrice(item.getTradeRentPrice());
        tradeArticle.setTradeDealPrice(item.getTradeDealPrice());
        tradeArticle.setTradeDayClusterCode(item.getTradeDayClusterCode());
        tradeArticle.setTradeDayClusterName(item.getTradeDayClusterName());
        tradeArticle.setTradeYearMonth(item.getTradeYearMonth());
        tradeArticle.setSameAddrPremiumMin(item.getSameAddrPremiumMin());
        return tradeArticle;
    }

    @Override
    public TradeArticle process(TradeArticle tradeItem) {
        return tradeItem;
    }

    @Override
    public List<TradeArticle> write(List<TradeArticle> tradeItems) {
        log.info("write : {}", tradeItems.size());
        return tradeArticleRepository.saveAll(tradeItems);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
