package net.moboo.batch.job;

import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.domain.TradeArticle;
import net.moboo.batch.domain.TradeItem;
import net.moboo.batch.hgnn.service.NaverTradeService;
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

import java.util.List;

@Slf4j // log 사용을 위한 lombok 어노테이션
@Configuration
public class NaverTradeJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final NaverTradeService naverTradeService;

    public NaverTradeJobConfiguration(JobBuilderFactory jobBuilderFactory,
                                      StepBuilderFactory stepBuilderFactory,
                                      NaverTradeService naverTradeService) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.naverTradeService = naverTradeService;
    }

    @Bean
    public Job naverTradeJob() {
        return jobBuilderFactory.get("naverTradeJob")
                                .start(naverTradeStep())
                                .incrementer(new UniqueRunIdIncrementer())
                                .build();
    }

    @Bean
    @JobScope
    public Step naverTradeStep() {
        return stepBuilderFactory.get("naverTradeStep")
            .<TradeArticle, TradeArticle>chunk(100) //(2)
                                                  .reader(naverTradeReader("11")) //(3)
//                                                  .processor(naverTradeProcessor())
                                                  .writer(naverTradeWriter())
//                                                             .listener(tradeJobListsener())
                                                  .build();
    }

    @Bean
    @StepScope
    public QueueItemReader<TradeArticle> naverTradeReader(@Value("#{jobParameters[parentRegionCode]}") String parentRegionCode) {
        return new QueueItemReader<>(naverTradeService.read(parentRegionCode)); //(3)
    }

    @Bean
    @StepScope
    public ItemProcessor<TradeArticle, TradeArticle> naverTradeProcessor() {
        return naverTradeService::process;
    }

    @Bean
    @StepScope
    public ItemWriter<TradeArticle> naverTradeWriter() {
        return ((List<? extends TradeArticle> tradeItems) -> naverTradeService.write((List<TradeArticle>) tradeItems));
    }
}
