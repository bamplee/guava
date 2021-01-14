//package net.moboo.batch.common.scheduler;
//
//import lombok.extern.slf4j.Slf4j;
//import net.moboo.batch.application.service.RentSyncService;
//import net.moboo.batch.application.service.TicketSyncService;
//import net.moboo.batch.application.service.TradeSyncService;
//import net.moboo.batch.domain.RegionType;
//import org.springframework.stereotype.Component;
//
//import java.time.YearMonth;
//
//@Slf4j
//@Component
//public class TradeScheduler {
//    private final TradeSyncService tradeSyncService;
//    private final TicketSyncService ticketSyncService;
//    private final RentSyncService rentSyncService;
//
//    public TradeScheduler(TradeSyncService tradeSyncService,
//                          TicketSyncService ticketSyncService,
//                          RentSyncService rentSyncService) {
//        this.tradeSyncService = tradeSyncService;
//        this.ticketSyncService = ticketSyncService;
//        this.rentSyncService = rentSyncService;
//    }
//
///*
//    @Scheduled(cron = "0 0 7,13,19 * * *")
//*/
//    public void sync() {
//        YearMonth currentYearMonth = YearMonth.now().minusMonths(4);
//        for (int i = 4; i >= 0; i--) {
//            ticketSyncService.syncOpenApiList(currentYearMonth);
//            ticketSyncService.syncDataList(currentYearMonth);
////            rentSyncService.syncOpenApiList(currentYearMonth);
////            rentSyncService.syncDataList(currentYearMonth);
//            currentYearMonth = currentYearMonth.plusMonths(1);
//        }
//    }
//}
