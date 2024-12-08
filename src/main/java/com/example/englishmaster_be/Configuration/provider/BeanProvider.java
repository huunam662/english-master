package com.example.englishmaster_be.Configuration.provider;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@FieldDefaults(level = AccessLevel.PUBLIC)
@Configuration
public class BeanProvider {

    @Bean
    CloseableHttpClient closeableHttpClient(){
        return HttpClients.createDefault();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
