package im.prize.api.application;

import com.google.common.collect.Lists;
import im.prize.api.application.dto.TradeDto;
import im.prize.api.domain.oboo.ApartmentMatchTable;
import im.prize.api.domain.oboo.OpenApiTradeInfo;
import im.prize.api.domain.oboo.TradeItem;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.ApartmentMatchTableRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.AptAreaRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.AptInfoRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.OpenApiTradeInfoRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.oboo.TradeItemRepository;
import im.prize.api.interfaces.MoneyConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TradeServiceImpl implements TradeService {
    private static final Integer PAGE_SIZE = 30;

    private final ApartmentMatchTableRepository apartmentMatchTableRepository;
    private final OpenApiTradeInfoRepository openApiTradeInfoRepository;
    private final AptAreaRepository aptAreaRepository;
    private final AptInfoRepository aptInfoRepository;
    private final TradeItemRepository tradeItemRepository;

    public TradeServiceImpl(ApartmentMatchTableRepository apartmentMatchTableRepository,
                            OpenApiTradeInfoRepository openApiTradeInfoRepository,
                            AptAreaRepository aptAreaRepository,
                            AptInfoRepository aptInfoRepository,
                            TradeItemRepository tradeItemRepository) {
        this.apartmentMatchTableRepository = apartmentMatchTableRepository;
        this.openApiTradeInfoRepository = openApiTradeInfoRepository;
        this.aptAreaRepository = aptAreaRepository;
        this.aptInfoRepository = aptInfoRepository;
        this.tradeItemRepository = tradeItemRepository;
    }

    @Override
    public List<TradeDto> getTrade(String aptId, Integer page, String area) {
        Optional<ApartmentMatchTable> byId = apartmentMatchTableRepository.findById(Long.valueOf(aptId));
        if (byId.isPresent()) {
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            ApartmentMatchTable apartmentMatchTable = byId.get();
            List<OpenApiTradeInfo> tradeList = Lists.newArrayList();
            if (StringUtils.isEmpty(area)) {
                tradeList =
                    openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAptNameOrderByDateDesc(apartmentMatchTable.getDongSigunguCode(),
                                                                                                         apartmentMatchTable.getDongCode(),
                                                                                                         apartmentMatchTable.getAptName(),
                                                                                                         pageable);
            } else {
                tradeList =
                    openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAptNameAndAreaOrderByDateDesc(apartmentMatchTable.getDongSigunguCode(),
                                                                                                                apartmentMatchTable.getDongCode(),
                                                                                                                apartmentMatchTable.getAptName(),
                                                                                                                Double.valueOf(area),
                                                                                                                pageable);
            }
            return tradeList.stream()
                            .map(this::transform)
                            .peek(x -> x.setArea(this.convertArea(this.getArea(String.valueOf(apartmentMatchTable.getId()), x.getArea()))))
                            .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    @Override
    public List<TradeDto> getNaverTrade(String aptId, Integer page) {
        Optional<ApartmentMatchTable> apartmentMatchTable = apartmentMatchTableRepository.findById(Long.valueOf(aptId));
        if (apartmentMatchTable.isPresent()) {
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            String portalId = apartmentMatchTable.get().getPortalId();
            return tradeItemRepository.findByHcpcNo(portalId, pageable)
                                      .stream()
                                      .map(this::transform)
                                      .peek(x -> x.setArea(this.getArea(aptId, x.getArea())))
                                      .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    @Override
    public List<TradeDto> getChartData(String aptId, Integer beforeMonths) {
        Optional<ApartmentMatchTable> byId = apartmentMatchTableRepository.findById(Long.valueOf(aptId));
        if (byId.isPresent()) {
            ApartmentMatchTable apartmentMatchTable = byId.get();
            List<OpenApiTradeInfo> tradeList =
                openApiTradeInfoRepository.findByDongSigunguCodeAndDongCodeAndAptNameAndDateGreaterThan(
                    apartmentMatchTable.getDongSigunguCode(),
                    apartmentMatchTable.getDongCode(),
                    apartmentMatchTable.getAptName(),
                    LocalDate.now().minusMonths(beforeMonths).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            return tradeList.stream()
                            .map(this::chartTransform)
                            .peek(x -> x.setArea(this.convertArea(this.getArea(String.valueOf(apartmentMatchTable.getId()), x.getArea()))))
                            .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private TradeDto transform(TradeItem tradeItem) {
        return TradeDto.builder()
                       .date(LocalDate.parse(tradeItem.getStartDate(), DateTimeFormatter.ofPattern("yyyyMMdd"))
                                      .format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                       .floor(tradeItem.getFlrInfo().split("/")[0])
                       .area(tradeItem.getSpc2())
                       .price(MoneyConverter.convertToMoneyAbbreviation(tradeItem.getPrcInfo()))
                       .build();
    }

    private String getArea(String aptId, String area) {
        return aptAreaRepository.findByAptIdAndPrivateArea(aptId, area)
                                .stream()
                                .findFirst()
                                .map(aptArea -> this.convertArea(aptArea.getPublicArea()))
                                .orElse(area);
    }

    private String convertArea(String area) {
        return String.valueOf((int) Math.ceil(Double.parseDouble(area)));
    }

    private TradeDto transform(OpenApiTradeInfo openApiTradeInfo) {
        String month = openApiTradeInfo.getMonth().length() == 1 ? String.format("0%s",
                                                                                 openApiTradeInfo.getMonth()) : openApiTradeInfo.getMonth();
        String day = openApiTradeInfo.getDay().length() == 1 ? String.format("0%s", openApiTradeInfo.getDay()) : openApiTradeInfo.getDay();
        String date = openApiTradeInfo.getYear() + month + day;

        return TradeDto.builder()
                       .date(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"))
                                      .format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                       .floor(openApiTradeInfo.getFloor())
                       .area(String.valueOf(openApiTradeInfo.getArea()))
                       .price(MoneyConverter.convertToLandMoneyAbbreviation(openApiTradeInfo.getPrice().replace(",", "")))
                       .build();
    }

    private TradeDto chartTransform(OpenApiTradeInfo openApiTradeInfo) {
        String month = openApiTradeInfo.getMonth().length() == 1 ? String.format("0%s",
                                                                                 openApiTradeInfo.getMonth()) : openApiTradeInfo.getMonth();
        String day = openApiTradeInfo.getDay().length() == 1 ? String.format("0%s", openApiTradeInfo.getDay()) : openApiTradeInfo.getDay();
        String date = openApiTradeInfo.getYear() + month + day;

        return TradeDto.builder()
                       .date(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"))
                                      .format(DateTimeFormatter.ofPattern("yyyyMMdd")))
//                       .area(String.valueOf((int) Math.ceil(Double.parseDouble(openApiTradeInfo.getArea()))))
                       .area(String.valueOf(openApiTradeInfo.getArea()))
                       .price(openApiTradeInfo.getPrice().replace(",", ""))
                       .build();
    }
}
