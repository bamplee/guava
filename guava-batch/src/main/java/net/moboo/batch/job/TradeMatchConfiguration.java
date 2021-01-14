package net.moboo.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.application.service.TradeMatchService;
import net.moboo.batch.domain.OpenApiTradeInfo;
import net.moboo.batch.hgnn.repository.ApartmentMatchTable;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration
public class TradeMatchConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TradeMatchService tradeMatchService;

    @Bean
    public Job tradeMatchJob() {
        return jobBuilderFactory.get("tradeMatchJob")
                                .start(tradeMatchStep())
                                .incrementer(new UniqueRunIdIncrementer())
                                .build();
    }

    @Bean
    public Step tradeMatchStep() {
        return stepBuilderFactory.get("tradeMatchStep")
            .<ApartmentMatchTable, ApartmentMatchTable>chunk(200) //(2)
                                                                   .reader(tradeMatcReader()) //(3)
                                                                   .processor(tradeMatcProcessor())
                                                                   .writer(tradeMatchWriter())
                                                                   .build();
    }

    @Bean
    @StepScope
    public QueueItemReader<ApartmentMatchTable> tradeMatcReader() {
        return new QueueItemReader<>(tradeMatchService.read()); //(3)
    }

    @Bean
    @StepScope
    public ItemProcessor<ApartmentMatchTable, ApartmentMatchTable> tradeMatcProcessor() {
        return tradeMatchService::process;
    }

    @Bean
    @StepScope
    public ItemWriter<ApartmentMatchTable> tradeMatchWriter() {
        return (List<? extends ApartmentMatchTable> apartmentMatchTableList) -> tradeMatchService.write((List<ApartmentMatchTable>) apartmentMatchTableList);
    }
}
