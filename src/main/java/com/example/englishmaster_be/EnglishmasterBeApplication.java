package com.example.englishmaster_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class EnglishmasterBeApplication {

    private static final String local = "local";

    private static final String staging = "staging";

    public static void main(String[] args) {

        SpringApplication application = new SpringApplication(EnglishmasterBeApplication.class);

        ConfigurableEnvironment environment  = new StandardEnvironment();
        environment.setActiveProfiles(staging);
        application.setEnvironment(environment);

        application.run(args);
    }

}
