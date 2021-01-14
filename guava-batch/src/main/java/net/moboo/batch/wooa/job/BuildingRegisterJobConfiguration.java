package net.moboo.batch.wooa.job;

import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.job.QueueItemReader;
import net.moboo.batch.job.UniqueRunIdIncrementer;
import net.moboo.batch.wooa.repository.BuildingRegister;
import net.moboo.batch.wooa.service.KaptService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j // log 사용을 위한 lombok 어노테이션
@Configuration
public class BuildingRegisterJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final KaptService kaptService;

    public BuildingRegisterJobConfiguration(JobBuilderFactory jobBuilderFactory,
                                            StepBuilderFactory stepBuilderFactory,
                                            KaptService kaptService) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.kaptService = kaptService;
    }

    @Bean
    public Job buildingRegisterJob() {
        return jobBuilderFactory.get("buildingRegisterJob")
                                .start(buildingRegisterStep())
                                .incrementer(new UniqueRunIdIncrementer())
                                .build();
    }

    @Bean
    @JobScope
    public Step buildingRegisterStep() {
        return stepBuilderFactory.get("buildingRegisterStep")
            .<BuildingRegister, BuildingRegister>chunk(100) //(2)
                                                            .reader(buildingRegisterReader()) //(3)
//                                            .processor(buildingRegisterProcessor())
                                                            .writer(buildingRegisterWriter())
//                                                             .listener(tradeJobListsener())
                                                            .build();
    }

    @Bean
    @StepScope
    public QueueItemReader<BuildingRegister> buildingRegisterReader() {
        return new QueueItemReader<>(kaptService.readAllByBuildingRegister()); //(3)
    }

    @Bean
    @StepScope
    public ItemProcessor<BuildingRegister, BuildingRegister> buildingRegisterProcessor() {
        return kaptService::processByBuildingRegister;
    }

    @Bean
    @StepScope
    public ItemWriter<BuildingRegister> buildingRegisterWriter() {
        return ((List<? extends BuildingRegister> buildingRegisterList) -> kaptService.writeByBuildingRegister((List<BuildingRegister>) buildingRegisterList));
    }
}
