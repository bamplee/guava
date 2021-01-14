package net.moboo.batch.wooa.job;

import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.hgnn.repository.RegionTemp;
import net.moboo.batch.job.QueueItemReader;
import net.moboo.batch.job.UniqueRunIdIncrementer;
import net.moboo.batch.wooa.repository.KaptCode;
import net.moboo.batch.wooa.repository.KaptCodeRepository;
import net.moboo.batch.wooa.repository.KaptInfo;
import net.moboo.batch.wooa.service.KaptService;
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

@Slf4j // log 사용을 위한 lombok 어노테이션
@Configuration
public class KaptInfoJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final KaptService kaptService;
    private final KaptCodeRepository kaptCodeRepository;

    public KaptInfoJobConfiguration(JobBuilderFactory jobBuilderFactory,
                                    StepBuilderFactory stepBuilderFactory,
                                    KaptService kaptService,
                                    KaptCodeRepository kaptCodeRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.kaptService = kaptService;
        this.kaptCodeRepository = kaptCodeRepository;
    }

    @Bean
    public Job kaptInfoJob() {
        return jobBuilderFactory.get("kaptInfoJob")
                                .start(kaptInfoStep())
                                .incrementer(new UniqueRunIdIncrementer())
                                .build();
    }

    @Bean
    @JobScope
    public Step kaptInfoStep() {
        return stepBuilderFactory.get("kaptInfoStep")
            .<KaptCode, KaptInfo>chunk(100) //(2)
                                            .reader(kaptInfoReader()) //(3)
                                            .processor(kaptInfoProcessor())
                                            .writer(kaptInfoWriter())
//                                                             .listener(tradeJobListsener())
                                            .build();
    }

//    @Bean
//    @StepScope
//    public RepositoryItemReader<KaptCode> kaptInfoReader() {
//        RepositoryItemReader<KaptCode> reader = new RepositoryItemReader<>();
//        reader.setRepository(kaptCodeRepository);
//        reader.setMethodName("findAll");
//        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
//        return reader;
//    }

    @Bean
    @StepScope
    public QueueItemReader<KaptCode> kaptInfoReader() {
        return new QueueItemReader<>(kaptService.readAll()); //(3)
    }

    @Bean
    @StepScope
    public ItemProcessor<KaptCode, KaptInfo> kaptInfoProcessor() {
        return kaptService::processByKaptInfo;
    }

    @Bean
    @StepScope
    public ItemWriter<KaptInfo> kaptInfoWriter() {
        return ((List<? extends KaptInfo> kaptInfoList) -> kaptService.writeByKaptInfo((List<KaptInfo>) kaptInfoList));
    }
}
