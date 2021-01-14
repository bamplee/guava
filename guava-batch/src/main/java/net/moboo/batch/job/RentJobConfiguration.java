package net.moboo.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.application.service.RentSyncService;
import net.moboo.batch.domain.OpenApiRentInfo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.YearMonth;
import java.util.List;

@Slf4j // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration
public class RentJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final RentSyncService rentSyncService;
    public static YearMonth YEAR_MONTH = YearMonth.now();

    @Bean
    public RentJobListener rentJobListener() {
        return new RentJobListener();
    }

    @Bean
    public Job rentJob() {
        return jobBuilderFactory.get("rentJob")
                                .start(rentStep())
                                .on("CONTINUE")
                                .to(rentStep())
                                .on("CONTINUE")
                                .to(rentStep())
                                .on("FINISHED")
                                .end()
                                .end()
                                .build();
    }

    @Bean
    public Step rentStep() {
        return stepBuilderFactory.get("rentStep")
            .<OpenApiRentInfo, OpenApiRentInfo>chunk(1000) //(2)
                                                           .reader(rentReader()) //(3)
//                                                           .processor(rentProcessor())
                                                           .writer(rentWriter())
                                                           .listener(rentJobListener())
                                                           .build();
    }

    @Bean
    @StepScope
    public QueueItemReader<OpenApiRentInfo> rentReader() {
        return new QueueItemReader<>(rentSyncService.getOpenApiRentInfo(YEAR_MONTH)); //(3)
    }

    @Bean
    @StepScope
    public ItemProcessor<OpenApiRentInfo, OpenApiRentInfo> rentProcessor() {
        return rentSyncService::process;
    }

    @Bean
    @StepScope
    public ItemWriter<OpenApiRentInfo> rentWriter() {
        return ((List<? extends OpenApiRentInfo> openApiRentInfos) -> rentSyncService.setOpenApiRentInfo((List<OpenApiRentInfo>) openApiRentInfos));
    }
}
