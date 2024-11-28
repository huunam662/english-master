package com.example.englishmaster_be.Configuration.provider;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@FieldDefaults(level = AccessLevel.PUBLIC)
@Configuration
public class BeanProvider {

    @Bean
    CloseableHttpClient closeableHttpClient(){
        return HttpClients.createDefault();
    }

}
