//package net.moboo.batch.application.service;
//
//import net.moboo.batch.domain.Region;
//import net.moboo.batch.domain.RegionType;
//import net.moboo.batch.domain.Ticket;
//import net.moboo.batch.domain.TicketRank;
//import net.moboo.batch.infrastructure.jpa.RegionRepository;
//import net.moboo.batch.infrastructure.jpa.TicketRankRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import static net.moboo.batch.domain.TradeRanks.UNDETERMINED;
//
//@Service
//public class TicketRankScheduleServiceImpl implements TicketRankScheduleService {
//    private final TicketRankRepository ticketRanksRepository;
//    private final RegionRepository regionRepository;
//
//    public TicketRankScheduleServiceImpl(TicketRankRepository ticketRanksRepository,
//                                         RegionRepository regionRepository) {
//        this.ticketRanksRepository = ticketRanksRepository;
//        this.regionRepository = regionRepository;
//    }
//
//    @Override
//    public void syncTicketRanks(List<Ticket> tickets, String yyyyMmDate, Region region, RegionType regionType) {
//        Map<String, List<Ticket>> ticketMap = groupByLowerRegion(tickets, regionType);
//        if (CollectionUtils.isEmpty(ticketMap)) {
//            return;
//        }
//        updateCountRank(ticketMap, region, yyyyMmDate);
//    }
//
//    private void updateCountRank(Map<String, List<Ticket>> ticketMap, Region region, String yyyyMmDate) {
//        Map<String, Rank<Long>> newTicketCountRanks = Rank.aggregateByRank(ticketMap, tickets -> {
//            Integer count = tickets.size();
//            return count.longValue();
//        });
//        updateCountRankWithNewRank(region, yyyyMmDate, newTicketCountRanks);
//    }
//
//    private void updateCountRankWithNewRank(Region region,
//                                            String yyyyMmDate,
//                                            Map<String, Rank<Long>> newTicketCountRank) {
//        List<TicketRank> beforeTicketCountRanks = ticketRanksRepository.findByRegionAndDate(region, yyyyMmDate);
//        List<TicketRank> newTicketRanks = new ArrayList<>();
//
//        for (TicketRank beforeTicketCountRank : beforeTicketCountRanks) {
//            Rank<Long> newRank = newTicketCountRank.get(beforeTicketCountRank.getRegionCode());
//            if (newRank != null) {
//                beforeTicketCountRank.setCount(newRank.getValue());
//                beforeTicketCountRank.setCountRanking(newRank.getRanking());
//                newTicketCountRank.remove(beforeTicketCountRank.getRegionCode());
//            } else {
//                beforeTicketCountRank.setCount(0);
//                beforeTicketCountRank.setCountRanking(UNDETERMINED);
//            }
//        }
//
//        newTicketCountRank.forEach(
//            (regionCode, newRank) -> newTicketRanks.add(TicketRank.builder()
//                                                                 .withCount(newRank.getValue())
//                                                                 .withCountRanking(newRank.getRanking())
//                                                                 .withDate(yyyyMmDate)
//                                                                 .withRegionCode(regionCode)
//                                                                 .withRegionType(RegionType.getLowerRegionType(region.getType()))
//                                                                 .withRegion(region)
//                                                                 .withRegionName(getRegionNameByCode(regionCode,
//                                                                                                     RegionType.getLowerRegionType(region.getType())))
//                                                                 .build()));
//
//        ticketRanksRepository.saveAll(newTicketRanks);
//        ticketRanksRepository.saveAll(beforeTicketCountRanks);
//    }
//
//    private Map<String, List<Ticket>> groupByLowerRegion(List<Ticket> tickets, RegionType regionType) {
//        if (regionType == RegionType.SIDO) {
//            return tickets.stream()
//                          .collect(Collectors.groupingBy(Ticket::getGunguCode));
//        } else if (regionType == RegionType.SIGUNGU) {
//            return tickets.stream()
//                          .collect(Collectors.groupingBy(Ticket::getDongCode));
//        } else if (regionType == RegionType.DONG) {
//            return tickets.stream()
//                          .collect(Collectors.groupingBy(Ticket::getName));
//        }
//        return Collections.emptyMap();
//    }
//
//    private String getRegionNameByCode(String code, RegionType regionType) {
//        Region region = regionRepository.findByCodeAndType(code, regionType);
//        if (region == null) {
//            return code;
//        }
//        return region.getFullName();
//    }
//
//}
