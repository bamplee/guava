package net.moboo.batch.infrastructure;

import feign.Feign;
import feign.Retryer;
import net.moboo.batch.infrastructure.feign.AptRentApiClient;
import net.moboo.batch.infrastructure.feign.AptTradeApiClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableFeignClients
@EnableJpaRepositories("net.moboo.batch")
@EnableJpaAuditing
public class InfrastructureConfig {
}
