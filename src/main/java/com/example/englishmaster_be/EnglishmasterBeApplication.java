package com.example.englishmaster_be;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;


@SpringBootApplication
public class EnglishmasterBeApplication {

    private static final String local = "local";

    public static void main(String[] args) {

        SpringApplication application = new SpringApplication(EnglishmasterBeApplication.class);

        boolean isLocalhost = InetAddress.getLoopbackAddress()
                .getHostName().equalsIgnoreCase("localhost");

        if(isLocalhost) application.setAdditionalProfiles(local);

        application.run(args);

    }
}
