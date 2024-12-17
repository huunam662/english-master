package com.example.englishmaster_be.value;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OxfordValue {

    @Value("${oxford.api.appId}")
    String appId;

    @Value("${oxford.api.appKey}")
    String appKey;

    @Value("${oxford.api.baseUrl}")
    String baseUrl;

}
