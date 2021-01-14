package net.moboo.batch.job;

import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.application.service.TradeSyncService;
import net.moboo.batch.domain.OpenApiTradeInfo;
import net.moboo.batch.hgnn.repository.GuavaMappingInfoRepository;
import net.moboo.batch.hgnn.repository.GuavaRegion;
import net.moboo.batch.hgnn.repository.GuavaRegionRepository;
import net.moboo.batch.infrastructure.jpa.GuavaRegionStats;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j // log 사용을 위한 lombok 어노테이션
@Configuration
public class TradeStatsJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TradeSyncService tradeSyncService;
    private final GuavaMappingInfoRepository guavaMappingInfoRepository;
    private final GuavaRegionRepository guavaRegionRepository;

    public TradeStatsJobConfiguration(JobBuilderFactory jobBuilderFactory,
                                      StepBuilderFactory stepBuilderFactory,
                                      TradeSyncService tradeSyncService,
                                      GuavaMappingInfoRepository guavaMappingInfoRepository,
                                      GuavaRegionRepository guavaRegionRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.tradeSyncService = tradeSyncService;
        this.guavaMappingInfoRepository = guavaMappingInfoRepository;
        this.guavaRegionRepository = guavaRegionRepository;
    }

    @Bean
    public Job tradeStatsJob() {
        return jobBuilderFactory.get("tradeStatsJob")
                                .start(tradeStatsStep())
                                .incrementer(new UniqueRunIdIncrementer())
                                .build();
    }

    @Bean
    @JobScope
    public Step tradeStatsStep() {
        return stepBuilderFactory.get("tradeStatsStep")
            .<GuavaRegionStats, GuavaRegionStats>chunk(1000) //(2)
                                                        .reader(tradeStatsReader()) //(3)
//                                                        .processor(tradeStatsProcessor())
                                                        .writer(tradeStatsWriter())
//                                                             .listener(tradeJobListsener())
                                                        .build();
    }
//
//    @Bean
//    @StepScope
//    public RepositoryItemReader<GuavaRegion> tradeStatsReader() {
//        RepositoryItemReader<GuavaRegion> reader = new RepositoryItemReader<>();
//        reader.setRepository(guavaRegionRepository);
//        reader.setMethodName("findAll");
//        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
//        return reader;
//    }

    @Bean
    @StepScope
    public QueueItemReader<GuavaRegionStats> tradeStatsReader() {
        return new QueueItemReader<>(tradeSyncService.tradeStatsRead()); //(3)
    }

//    @Bean
//    @StepScope
//    public ItemProcessor<GuavaRegion, GuavaRegionStats> tradeStatsProcessor() {
//        return tradeSyncService::tradeStatsProcess;
//    }

    @Bean
    @StepScope
    public ItemWriter<GuavaRegionStats> tradeStatsWriter() {
        return ((List<? extends GuavaRegionStats> tradeItems) -> tradeSyncService.tradeStatsWrite((List<GuavaRegionStats>) tradeItems.stream()
                                                                                                                                     .filter(
                                                                                                                                         x -> ObjectUtils
                                                                                                                                             .isNotEmpty(
                                                                                                                                                 x))
                                                                                                                                     .collect(
                                                                                                                                         Collectors
                                                                                                                                             .toList())));
    }
}
