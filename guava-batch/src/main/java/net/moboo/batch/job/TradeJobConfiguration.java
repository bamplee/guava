package net.moboo.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.application.service.TradeSyncService;
import net.moboo.batch.domain.OpenApiTradeInfo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration
public class TradeJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TradeSyncService tradeSyncService;
    public static YearMonth YEAR_MONTH = YearMonth.now();

    @Bean
    public TradeJobListener tradeJobListsener() {
        return new TradeJobListener();
    }

    @Bean
    public Job tradeJob() {
        return jobBuilderFactory.get("tradeJob")
                                .start(tradeStep())
                                .on("CONTINUE")
                                .to(tradeStep())
                                .on("CONTINUE")
                                .to(tradeStep())
                                .on("FINISHED")
                                .end()
                                .end()
                                .build();
    }

    @Bean
    public Step tradeStep() {
        return stepBuilderFactory.get("tradeStep")
            .<OpenApiTradeInfo, OpenApiTradeInfo>chunk(1000) //(2)
                                                             .reader(tradeReader()) //(3)
//                                                             .processor(tradeProcessor())
                                                             .writer(tradeWriter())
                                                             .listener(tradeJobListsener())
                                                             .build();
    }

    @Bean
    @StepScope
    public QueueItemReader<OpenApiTradeInfo> tradeReader() {
        return new QueueItemReader<>(tradeSyncService.getOpenApiTradeInfo(YEAR_MONTH)); //(3)
    }

    @Bean
    @StepScope
    public ItemProcessor<OpenApiTradeInfo, OpenApiTradeInfo> tradeProcessor() {
        return tradeSyncService::process;
    }

    @Bean
    @StepScope
    public ItemWriter<OpenApiTradeInfo> tradeWriter() {
        return ((List<? extends OpenApiTradeInfo> openApiTradeInfos) -> tradeSyncService.setOpenApiTradeInfo((List<OpenApiTradeInfo>) openApiTradeInfos));
    }
}
