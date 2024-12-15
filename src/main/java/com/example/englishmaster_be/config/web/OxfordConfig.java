package com.example.englishmaster_be.config.web;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "oxford.api")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OxfordConfig {

    String appId;

    String appKey;

    String baseUrl;

}
