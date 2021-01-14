package net.moboo.batch.job;

import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.hgnn.repository.GuavaRegion;
import net.moboo.batch.hgnn.repository.RegionTemp;
import net.moboo.batch.hgnn.repository.RegionTempRepository;
import net.moboo.batch.hgnn.service.GuavaMatchJobService;
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

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Slf4j // log 사용을 위한 lombok 어노테이션
@Configuration
public class GuavaRegionMatchJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final GuavaMatchJobService guavaMatchJobService;
    private final RegionTempRepository regionTempRepository;

    public GuavaRegionMatchJobConfiguration(JobBuilderFactory jobBuilderFactory,
                                            StepBuilderFactory stepBuilderFactory,
                                            GuavaMatchJobService guavaMatchJobService,
                                            RegionTempRepository regionTempRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.guavaMatchJobService = guavaMatchJobService;
        this.regionTempRepository = regionTempRepository;
    }

    @Bean
    public Job guavaRegionMatchJob() {
        return jobBuilderFactory.get("guavaRegionMatchJob")
                                .start(guavaRegionMatchStep())
                                .incrementer(new UniqueRunIdIncrementer())
                                .build();
    }

    @Bean
    @JobScope
    public Step guavaRegionMatchStep() {
        return stepBuilderFactory.get("guavaRegionMatchStep")
            .<RegionTemp, GuavaRegion>chunk(100) //(2)
                                                 .reader(guavaRegionMatchReader()) //(3)
                                                 .processor(guavaRegionMatchProcessor())
                                                 .writer(guavaRegionMatchWriter())
//                                                             .listener(tradeJobListsener())
                                                 .build();
    }

//    @Bean
//    @StepScope
//    public QueueItemReader<RegionTemp> guavaRegionMatchReader() {
//        return new QueueItemReader<>(guavaMatchJobService.read()); //(3)
//    }

    @Bean
    public RepositoryItemReader<RegionTemp> guavaRegionMatchReader() {
        RepositoryItemReader<RegionTemp> reader = new RepositoryItemReader<>();
        reader.setRepository(regionTempRepository);
        reader.setMethodName("findAll");
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<RegionTemp, GuavaRegion> guavaRegionMatchProcessor() {
        return guavaMatchJobService::processRegion;
    }

    @Bean
    @StepScope
    public ItemWriter<GuavaRegion> guavaRegionMatchWriter() {
        return ((List<? extends GuavaRegion> tradeItems) -> guavaMatchJobService.writeRegion((List<GuavaRegion>) tradeItems));
    }
}
