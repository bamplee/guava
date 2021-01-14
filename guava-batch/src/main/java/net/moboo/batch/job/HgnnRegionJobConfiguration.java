//package net.moboo.batch.job;
//
//import lombok.extern.slf4j.Slf4j;
//import net.moboo.batch.hgnn.repository.GuavaRegion;
//import net.moboo.batch.hgnn.repository.RegionTempRepository;
//import net.moboo.batch.infrastructure.jpa.PbRegionCode;
//import net.moboo.batch.infrastructure.jpa.PbRegionCodeRepository;
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
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j // log 사용을 위한 lombok 어노테이션
//@Configuration
//public class HgnnRegionJobConfiguration {
//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//    private final RegionTempRepository regionTempRepository;
//    private final PbRegionCodeRepository pbRegionCodeRepository;
//    private final HgnnService hgnnService;
//
//    public HgnnRegionJobConfiguration(JobBuilderFactory jobBuilderFactory,
//                                      StepBuilderFactory stepBuilderFactory,
//                                      RegionTempRepository regionTempRepository,
//                                      PbRegionCodeRepository pbRegionCodeRepository,
//                                      HgnnService hgnnService) {
//        this.jobBuilderFactory = jobBuilderFactory;
//        this.stepBuilderFactory = stepBuilderFactory;
//        this.regionTempRepository = regionTempRepository;
//        this.pbRegionCodeRepository = pbRegionCodeRepository;
//        this.hgnnService = hgnnService;
//    }
//
//    @Bean
//    public Job HgnnRegionJob() {
//        return jobBuilderFactory.get("hgnnRegionJob")
//                                .start(hgnnRegionStep())
//                                .incrementer(new UniqueRunIdIncrementer())
//                                .build();
//    }
//
//    @Bean
//    @JobScope
//    public Step hgnnRegionStep() {
//        return stepBuilderFactory.get("hgnnRegionStep")
//            .<PbRegionCode, GuavaRegion>chunk(100) //(2)
//                                                   .reader(hgnnRegionReader()) //(3)
//                                                   .processor(hgnnRegionProcessor())
//                                                   .writer(hgnnRegionWriter())
////                                                             .listener(tradeJobListsener())
//                                                   .build();
//    }
//
//    @Bean
//    public RepositoryItemReader<PbRegionCode> hgnnRegionReader() {
//        RepositoryItemReader<PbRegionCode> reader = new RepositoryItemReader<>();
//        reader.setRepository(pbRegionCodeRepository);
//        reader.setMethodName("findAll");
//        reader.setSort(Collections.singletonMap("code", Sort.Direction.ASC));
//        return reader;
//    }
//
////    @Bean
////    @StepScope
////    public QueueItemReader<PbRegionCode> hgnnRegionReader() {
////        return new QueueItemReader<>(hgnnService.read()); //(3)
////    }
//
//    @Bean
//    @StepScope
//    public ItemProcessor<PbRegionCode, GuavaRegion> hgnnRegionProcessor() {
//        return hgnnService::process;
//    }
//
//    @Bean
//    @StepScope
//    public ItemWriter<GuavaRegion> hgnnRegionWriter() {
//        return ((List<? extends GuavaRegion> tradeItems) -> hgnnService.write((List<GuavaRegion>) tradeItems.stream()
//                                                                                                            .filter(ObjectUtils::isNotEmpty)
//                                                                                                            .collect(Collectors.toList())));
//    }
//}
