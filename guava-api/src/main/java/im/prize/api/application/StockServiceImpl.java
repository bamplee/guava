package im.prize.api.application;

import com.google.common.collect.Lists;
import im.prize.api.application.dto.ArticleDto;
import im.prize.api.application.dto.CompanyActivityDto;
import im.prize.api.application.dto.DailyActivityDto;
import im.prize.api.application.dto.CompanyDto;
import im.prize.api.application.dto.PriceInfoDto;
import im.prize.api.datatool.DataGoStockClient;
import im.prize.api.datatool.GoogleNewsClient;
import im.prize.api.datatool.InfoStockClient;
import im.prize.api.datatool.NaverArticleClient;
import im.prize.api.datatool.NaverStockClient;
import im.prize.api.datatool.NewsApiClient;
import im.prize.api.datatool.YhNewsClient;
import im.prize.api.datatool.response.DataGoStockResponse;
import im.prize.api.datatool.response.NaverArticleResponse;
import im.prize.api.datatool.response.NaverStockOverallResponse;
import im.prize.api.datatool.response.NaverStockResponse;
import im.prize.api.datatool.response.NaverStockSearchResponse;
import im.prize.api.datatool.response.YhNewsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static im.prize.api.datatool.NaverArticleClient.DEFAULT_CLIENT_ID;
import static im.prize.api.datatool.NaverArticleClient.DEFAULT_CLIENT_SECRET;
import static im.prize.api.datatool.NaverArticleClient.DEFAULT_SORT;

@Slf4j
@Service
public class StockServiceImpl implements StockService {
    @Value("${datago.api.key}")
    private String serviceKey;
    @Value("${news.api.key}")
    private String newsServiceKey;

    private final NaverStockClient naverStockClient;
    private final GoogleNewsClient googleNewsClient;
    private final DataGoStockClient dataGoStockClient;
    private final NaverArticleClient naverArticleClient;
    private final NewsApiClient newsApiClient;
    private final YhNewsClient yhNewsClient;
    private final InfoStockClient infoStockClient;

    public StockServiceImpl(NaverStockClient naverStockClient,
                            GoogleNewsClient googleNewsClient,
                            DataGoStockClient dataGoStockClient,
                            NaverArticleClient naverArticleClient,
                            NewsApiClient newsApiClient, YhNewsClient yhNewsClient, InfoStockClient infoStockClient) {
        this.naverStockClient = naverStockClient;
        this.googleNewsClient = googleNewsClient;
        this.dataGoStockClient = dataGoStockClient;
        this.naverArticleClient = naverArticleClient;
        this.newsApiClient = newsApiClient;
        this.yhNewsClient = yhNewsClient;
        this.infoStockClient = infoStockClient;
    }

    @Override
    public List<CompanyDto> getCompanyList(String query) {
        return this.getCompanyList(query, "100", "1");
    }

    private List<CompanyDto> getCompanyList(String query, String numOfRows, String pageNo) {
        return dataGoStockClient.getStkIsinByNmN1(serviceKey, query, numOfRows, pageNo)
                                .getBody()
                                .getItems()
                                .getItem()
                                .stream()
                                .map(this::transform)
                                .collect(Collectors.toList());
    }

    private CompanyDto transform(DataGoStockResponse.Body.Items.Item item) {
        return CompanyDto.builder().name(item.getKorSecnNm()).id(item.getShotnIsin()).build();
    }

    @Override
    public List<DailyActivityDto> getActivityList(String query, Integer page, Integer size) {
        if (query != null && !"".equals(query)) {
            List<CompanyDto> companyList = this.getCompanyList(query, size.toString(), page.toString());
            return companyList.stream()
                              .map(x -> naverStockClient.getOverallHeaderItem(x.getId()).getResult())
                              .filter(Objects::nonNull)
                              .map(this::transform)
                              .collect(Collectors.toList());
        }
        NaverStockSearchResponse top_search = naverStockClient.search("top_search", size, page);
        return top_search.getResult().getItemList().stream().map(this::transform).collect(Collectors.toList());
    }

    private DailyActivityDto transform(NaverStockOverallResponse.Result item) {
        return DailyActivityDto.builder()
                               .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")))
                               .company(CompanyDto.builder().id(item.getCd()).name(item.getNm()).build())
                               .priceInfo(PriceInfoDto.builder()
                                                      .price(item.getNv())
                                                      .pointDelta(item.getCv())
                                                      .percentageDelta(Double.parseDouble(String.format("%.2f", item.getCr())))
                                                      .build())
                               .build();
    }

    @Override
    public PriceInfoDto getCurrentPriceInfo(String companyId) {
        return this.transform(naverStockClient.getPriceDayList(companyId, 1, 1).getResult().getList().stream().findFirst().get());
    }

    @Override
    public List<PriceInfoDto> getPrices(String companyId, String since) {
        LocalDate beforeDate = LocalDate.parse(since, DateTimeFormatter.ofPattern("yyyyMMdd"));
        long size = this.calcWorkDays(beforeDate, LocalDate.now());
        List<PriceInfoDto> result = naverStockClient.getPriceDayList(companyId, Math.toIntExact(size), 1)
                                                    .getResult()
                                                    .getList()
                                                    .stream()
                                                    .filter(x -> !beforeDate.isAfter(LocalDate.parse(x.getDt(),
                                                                                                     DateTimeFormatter.ofPattern(
                                                                                                         "yyyyMMdd"))))
                                                    .map(this::transform)
//                                                    .sorted(Comparator.comparingInt(p -> Math.abs(p.getPointDelta())))
                                                    .collect(Collectors.toList());
//        if (result.size() > 6) {
//            result = result.subList(result.size() - 6, result.size());
//        }
        return result.stream().sorted(Comparator.comparing(p -> this.parseDate(p.getDate()))).collect(Collectors.toList());
    }

    @Override
    public List<CompanyActivityDto> getCompanyActivities(String companyId, String since) {
        List<PriceInfoDto> prices = this.getPrices(companyId, since);
        List<PriceInfoDto> articlePrices = Lists.newArrayList(prices);
        if (prices.size() > 6) {
            articlePrices = prices.stream()
                                  .sorted(Comparator.comparingInt(p -> Math.abs(p.getPointDelta())))
                                  .collect(Collectors.toList())
                                  .subList(prices.size() - 6, prices.size());
        }
        return articlePrices.parallelStream()
                            .map(x -> this.transform(companyId, x))
                            .sorted(Comparator.comparing(p -> this.parseDate(p.getDate())))
                            .collect(Collectors.toList());
    }

    private LocalDate parseDate(String yyyyMMdd) {
        return LocalDate.parse(yyyyMMdd, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private CompanyActivityDto transform(String companyId, PriceInfoDto priceInfo) {
//        DataGoStockResponse.Body.Items.BuildingInfo item = dataGoStockClient.getStkIsinByShortIsinN1(serviceKey, companyId, "1", "1")
//                                                                    .getBody()
//                                                                    .getItems()
//                                                                    .getItem()
//                                                                    .stream()
//                                                                    .findFirst()
//                                                                    .get();

        NaverStockOverallResponse overallHeaderItem = naverStockClient.getOverallHeaderItem(companyId);

        return CompanyActivityDto.builder().date(priceInfo.getDate())
                                 .article(this.callNews(overallHeaderItem.getResult().getNm(), priceInfo.getDate()))
                                 .priceInfo(PriceInfoDto.builder()
                                                        .price(priceInfo.getPrice())
                                                        .pointDelta(priceInfo.getPointDelta())
                                                        .percentageDelta(priceInfo.getPercentageDelta()).build())
                                 .build();
    }

    public String removeTag(String html) throws Exception {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    private ArticleDto callNews(String query, String date) {
        try {
//            GoogleNewsResponse googleNewsResponse = googleNewsClient.googleNews(GoogleNewsRequest.builder()
//                                                                                                 .query(query)
//                                                                                                 .date(date)
//                                                                                                 .build());

            String startDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")).minusDays(1l).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String nowDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")).plusDays(1l).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            YhNewsResponse search = yhNewsClient.search("1", "news", query, startDate, endDate, "score");
            Optional<YhNewsResponse.Article> first = search.getList().stream().findFirst();
            if(first.isPresent()) {
                YhNewsResponse.Article article = first.get();
                return ArticleDto.builder()
                                 .source(this.removeTag(article.getCategory()))
                                 .title(this.removeTag(article.getTitle()))
                                 .url("http://m.yonhapnewstv.co.kr/news/" + article.getSequence())
                                 .build();
            }
            return ArticleDto.builder().build();
        } catch (Exception e) {
            log.info(e.getMessage());
            return ArticleDto.builder()
                             .build();
        }
    }

    private ArticleDto callNaverNews(String query, String date) {
        try {
            int start = 1;
            while (true) {
                NaverArticleResponse articleList = naverArticleClient.getArticleList(DEFAULT_CLIENT_ID,
                                                                                     DEFAULT_CLIENT_SECRET,
                                                                                     query,
                                                                                     100,
                                                                                     start,
                                                                                     DEFAULT_SORT);
                List<NaverArticleResponse.Item> items = articleList.getItems().stream().filter(x -> {
                    long size = ChronoUnit.DAYS.between(this.parseDate(new SimpleDateFormat("yyyyMMdd").format(x.getPubDate())),
                                                        this.parseDate(date));
                    return size >= 0;
                }).collect(Collectors.toList());

                if (items.size() > 0) {
                    break;
                } else {
                    start++;
                }

                if (start >= articleList.getTotal() / articleList.getDisplay()) {
                    break;
                }
            }
            return ArticleDto.builder()
                             .build();
        } catch (Exception e) {
            log.info(e.getMessage());
            return ArticleDto.builder()
                             .build();
        }
    }

    private PriceInfoDto transform(NaverStockResponse.Result.Item item) {
        return PriceInfoDto.builder()
                           .date(item.getDt())
                           .price(item.getNcv())
                           .pointDelta(item.getCv())
                           .percentageDelta(item.getCr() == null ? 0 : Double.parseDouble(String.format("%.2f", item.getCr())))
                           .build();
    }

    private DailyActivityDto transform(NaverStockSearchResponse.Result.Item item) {
        return DailyActivityDto.builder()
                               .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")))
                               .company(CompanyDto.builder().id(item.getCd()).name(item.getNm()).build())
                               .priceInfo(PriceInfoDto.builder()
                                                      .price(item.getNv())
                                                      .pointDelta(item.getCv())
                                                      .percentageDelta(Double.parseDouble(String.format("%.2f", item.getCr())))
                                                      .build())
                               .build();
    }

    public static long calcWorkDays(final LocalDate start, final LocalDate end) {
        final DayOfWeek startW = start.getDayOfWeek();
        final DayOfWeek endW = end.getDayOfWeek();

        final long days = ChronoUnit.DAYS.between(start, end);
        final long daysWithoutWeekends = days - 2 * ((days + startW.getValue()) / 7);

        //adjust for starting and ending on a Sunday:
        return daysWithoutWeekends + (startW == DayOfWeek.SUNDAY ? 1 : 0) + (endW == DayOfWeek.SUNDAY ? 1 : 0);
    }
}
