package com.example.englishmaster_be.value;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class OxfordValue {

    @Value("${oxford.api.appId}")
    private String appId;

    @Value("${oxford.api.appKey}")
    private String appKey;

    @Value("${oxford.api.baseUrl}")
    private String baseUrl;

}
