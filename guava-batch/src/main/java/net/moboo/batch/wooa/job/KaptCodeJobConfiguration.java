package net.moboo.batch.wooa.job;

import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.domain.TradeArticle;
import net.moboo.batch.job.QueueItemReader;
import net.moboo.batch.job.UniqueRunIdIncrementer;
import net.moboo.batch.wooa.datatool.KaptCodeResponse;
import net.moboo.batch.wooa.repository.KaptCode;
import net.moboo.batch.wooa.service.KaptService;
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
public class KaptCodeJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final KaptService kaptService;

    public KaptCodeJobConfiguration(JobBuilderFactory jobBuilderFactory,
                                    StepBuilderFactory stepBuilderFactory,
                                    KaptService kaptService) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.kaptService = kaptService;
    }

    @Bean
    public Job kaptCodeJob() {
        return jobBuilderFactory.get("kaptCodeJob")
                                .start(kaptCodeStep())
                                .incrementer(new UniqueRunIdIncrementer())
                                .build();
    }

    @Bean
    @JobScope
    public Step kaptCodeStep() {
        return stepBuilderFactory.get("kaptCodeStep")
            .<KaptCode, KaptCode>chunk(100) //(2)
                                            .reader(kaptCodeReader()) //(3)
//                                            .processor(kaptCodeProcessor())
                                            .writer(kaptCodeWriter())
//                                                             .listener(tradeJobListsener())
                                            .build();
    }

    @Bean
    @StepScope
    public QueueItemReader<KaptCode> kaptCodeReader() {
        return new QueueItemReader<>(kaptService.readAll()); //(3)
    }

    @Bean
    @StepScope
    public ItemProcessor<KaptCode, KaptCode> kaptCodeProcessor() {
        return kaptService::process;
    }

    @Bean
    @StepScope
    public ItemWriter<KaptCode> kaptCodeWriter() {
        return ((List<? extends KaptCode> kaptCodeList) -> kaptService.write((List<KaptCode>) kaptCodeList));
    }
}
