package net.moboo.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.application.service.TradeSummaryService;
import net.moboo.batch.wooa.repository.TradeSummary;
import net.moboo.batch.wooa.repository.BuildingMapping;
import net.moboo.batch.wooa.repository.BuildingMappingRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

@Slf4j // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration
public class TradeSummaryJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TradeSummaryService tradeSummaryService;
    private final BuildingMappingRepository buildingMappingRepository;

    public static YearMonth YEAR_MONTH = YearMonth.now();

    @Bean
    public Job tradeSummaryJob() {
        return jobBuilderFactory.get("tradeSummaryJob")
                                .start(tradeSummaryStep())
                                .incrementer(new UniqueRunIdIncrementer())
                                .build();
    }

    @Bean
    public Step tradeSummaryStep() {
        return stepBuilderFactory.get("tradeSummaryStep")
            .<BuildingMapping, List<TradeSummary>>chunk(10) //(2)
                                                              .reader(tradeSummaryReader()) //(3)
                                                              .processor(tradeSummaryProcessor())
                                                              .writer(tradeSummaryWriter())
                                                              .build();
    }

    @Bean
    public RepositoryItemReader<BuildingMapping> tradeSummaryReader() {
        RepositoryItemReader<BuildingMapping> reader = new RepositoryItemReader<>();
        reader.setRepository(buildingMappingRepository);
        reader.setMethodName("findAll");
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        return reader;
    }
//
//    @Bean
//    @StepScope
//    public QueueItemReader<TradeSummary> tradeReader() {
//        return new QueueItemReader<>(tradeSummaryService.read()); //(3)
//    }

    @Bean
    @StepScope
    public ItemProcessor<BuildingMapping, List<TradeSummary>> tradeSummaryProcessor() {
        return tradeSummaryService::process;
    }

    @Bean
    @StepScope
    public ItemWriter<? super List<TradeSummary>> tradeSummaryWriter() {
        return (openApiTradeInfos -> tradeSummaryService.write((List<List<TradeSummary>>) openApiTradeInfos));
    }
}
