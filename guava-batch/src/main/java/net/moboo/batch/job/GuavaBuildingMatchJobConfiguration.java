package net.moboo.batch.job;

import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.hgnn.repository.AptTemp;
import net.moboo.batch.hgnn.repository.AptTempRepository;
import net.moboo.batch.hgnn.repository.GuavaBuilding;
import net.moboo.batch.hgnn.repository.RegionTemp;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class GuavaBuildingMatchJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final GuavaMatchJobService guavaMatchJobService;
    private final AptTempRepository aptTempRepository;

    public GuavaBuildingMatchJobConfiguration(JobBuilderFactory jobBuilderFactory,
                                              StepBuilderFactory stepBuilderFactory,
                                              GuavaMatchJobService guavaMatchJobService,
                                              AptTempRepository aptTempRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.guavaMatchJobService = guavaMatchJobService;
        this.aptTempRepository = aptTempRepository;
    }

    @Bean
    public Job guavaBuildingMatchJob() {
        return jobBuilderFactory.get("guavaBuildingMatchJob")
                                .start(guavaBuildingMatchStep())
                                .incrementer(new UniqueRunIdIncrementer())
                                .build();
    }

    @Bean
    @JobScope
    public Step guavaBuildingMatchStep() {
        return stepBuilderFactory.get("guavaBuildingMatchStep")
            .<AptTemp, GuavaBuilding>chunk(30) //(2)
//                                                .reader(guavaBuildingMatchReader()) //(3)
                                                .reader(guavaBuildingMatchReader()) //(3)
                                                .processor(guavaBuildingMatchProcessor())
                                                .writer(guavaBuildingMatchWriter())
//                                                             .listener(tradeJobListsener())
                                                .build();
    }

    @Bean
    @StepScope
    public QueueItemReader<AptTemp> guavaBuildingMatchReader() {
        return new QueueItemReader<>(guavaMatchJobService.read()); //(3)
    }

//    @Bean
//    public RepositoryItemReader<AptTemp> guavaBuildingMatchReader() {
//        RepositoryItemReader<AptTemp> reader = new RepositoryItemReader<>();
//        reader.setRepository(aptTempRepository);
//        reader.setMethodName("findAll");
//        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
//        return reader;
//    }

    @Bean
    @StepScope
    public ItemProcessor<AptTemp, GuavaBuilding> guavaBuildingMatchProcessor() {
        return regionTemp -> guavaMatchJobService.processBuilding(regionTemp);
    }

    @Bean
    @StepScope
    public ItemWriter<GuavaBuilding> guavaBuildingMatchWriter() {
        return ((List<? extends GuavaBuilding> tradeItems) -> guavaMatchJobService.writeBuilding((List<GuavaBuilding>) tradeItems));
    }
}
