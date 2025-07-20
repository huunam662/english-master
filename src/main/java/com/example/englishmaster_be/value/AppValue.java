package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppValue {

    @Value("${app.api.endpoint.prefix}")
    private String endpoint_prefix;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private Integer batchSize;

}
