package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppValue {

    @Value("${app.api.endpoint.prefix}")
    String endpoint_prefix;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    Integer batchSize;

    @Value("${spring.jpa.properties.hibernate.default_batch_fetch_size}")
    Integer fetchBatchSize;

}
