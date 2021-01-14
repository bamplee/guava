//package net.moboo.batch.job;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.moboo.batch.hgnn.repository.ApartmentMatchTable;
//import net.moboo.batch.hgnn.service.HgnnService;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.JobScope;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//@Slf4j // log 사용을 위한 lombok 어노테이션
//@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
//@Configuration
//public class SimpleJobConfiguration {
//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//    private final HgnnService hgnnService;
//
//    @Bean
//    public Job simpleJob() {
//        return jobBuilderFactory.get("simpleJob")
//                                .start(simpleStep2(null))
//                                .build();
//    }
//
//    @Bean
//    @JobScope
//    public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
//        return stepBuilderFactory.get("simpleStep2")
//            .<ApartmentMatchTable, ApartmentMatchTable>chunk(100) //(2)
//                                                                 .reader(inactiveUserReader()) //(3)
//                                                                 .processor(inactiveUserProcessor())
//                                                                 .writer(inactiveUserWriter())
//                                                                 .build();
//    }
//
//    @Bean
//    @StepScope //(1)
//    public QueueItemReader<ApartmentMatchTable> inactiveUserReader() {
//        return new QueueItemReader<>(hgnnService.getAllApartmentMatchTableList()); //(3)
//    }
//
//    public ItemProcessor<ApartmentMatchTable, ApartmentMatchTable> inactiveUserProcessor() {
//        return hgnnService::transform;
//    }
//
//    public ItemWriter<ApartmentMatchTable> inactiveUserWriter() {
//        return ((List<? extends ApartmentMatchTable> apartmentMatchTables) -> hgnnService.setApartmentMatchTables((List<ApartmentMatchTable>) apartmentMatchTables));
//    }
//}
