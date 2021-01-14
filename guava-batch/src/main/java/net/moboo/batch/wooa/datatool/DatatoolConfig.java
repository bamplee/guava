package net.moboo.batch.wooa.datatool;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableFeignClients
//@EnableJpaRepositories("net.moboo.batch.wooa")
//@EnableJpaAuditing
public class DatatoolConfig {
}
