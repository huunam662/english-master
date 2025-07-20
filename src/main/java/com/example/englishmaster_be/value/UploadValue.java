package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class UploadValue {

    @Value("${upload.server.api.upload}")
    private String uploadApiUrl;

    @Value("${upload.server.token}")
    private String token;

    @Value("${upload.server.api.delete}")
    private String deleteApiUrl;

}
