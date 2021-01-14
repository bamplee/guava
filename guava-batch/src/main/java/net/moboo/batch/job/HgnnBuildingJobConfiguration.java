//package net.moboo.batch.job;
//
//import lombok.extern.slf4j.Slf4j;
//import net.moboo.batch.hgnn.repository.GuavaBuilding;
//import net.moboo.batch.hgnn.repository.GuavaRegion;
//import net.moboo.batch.hgnn.repository.GuavaRegionRepository;
//import net.moboo.batch.hgnn.service.GuavaHgnnBuilding;
//import net.moboo.batch.hgnn.service.HgnnService;
//import net.moboo.batch.infrastructure.jpa.PbRegionCode;
//import org.apache.commons.lang3.ObjectUtils;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.JobScope;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.data.RepositoryItemReader;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.Sort;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j // log 사용을 위한 lombok 어노테이션
//@Configuration
//public class HgnnBuildingJobConfiguration {
//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//    private final GuavaRegionRepository guavaRegionRepository;
//    private final HgnnService hgnnService;
//
//    public HgnnBuildingJobConfiguration(JobBuilderFactory jobBuilderFactory,
//                                        StepBuilderFactory stepBuilderFactory,
//                                        GuavaRegionRepository guavaRegionRepository,
//                                        HgnnService hgnnService) {
//        this.jobBuilderFactory = jobBuilderFactory;
//        this.stepBuilderFactory = stepBuilderFactory;
//        this.guavaRegionRepository = guavaRegionRepository;
//        this.hgnnService = hgnnService;
//    }
//
//    @Bean
//    public Job HgnnBuildingJob() {
//        return jobBuilderFactory.get("HgnnBuildingJob")
//                                .start(HgnnBuildingStep())
//                                .incrementer(new UniqueRunIdIncrementer())
//                                .build();
//    }
//
//    @Bean
//    @JobScope
//    public Step HgnnBuildingStep() {
//        return stepBuilderFactory.get("HgnnBuildingStep")
//            .<GuavaRegion, GuavaBuilding>chunk(10000) //(2)
//                                                         .reader(HgnnBuildingReader()) //(3)
////                                                         .processor(HgnnBuildingProcessor())
//                                                         .writer(HgnnBuildingWriter())
////                                                             .listener(tradeJobListsener())
//                                                         .build();
//    }
//
////    @Bean
////    public RepositoryItemReader<GuavaRegion> HgnnBuildingReader() {
////        RepositoryItemReader<GuavaRegion> reader = new RepositoryItemReader<>();
////        reader.setRepository(guavaRegionRepository);
////        reader.setMethodName("findAll");
////        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
////        return reader;
////    }
//
//    @Bean
//    @StepScope
//    public QueueItemReader<GuavaRegion> HgnnBuildingReader() {
//        return new QueueItemReader<>(hgnnService.readBuilding()); //(3)
//    }
//
//    @Bean
//    @StepScope
//    public ItemProcessor<GuavaRegion, GuavaBuilding> HgnnBuildingProcessor() {
//        return hgnnService::process;
//    }
//
//    @Bean
//    @StepScope
//    public ItemWriter<GuavaBuilding> HgnnBuildingWriter() {
//        return ((List<? extends GuavaBuilding> tradeItems) -> hgnnService.writeBuilding((List<GuavaBuilding>) tradeItems));
//    }
//}
