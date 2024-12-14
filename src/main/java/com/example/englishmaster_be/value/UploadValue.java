package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadValue {

    @Value("${upload.server.api.upload}")
    String uploadApiUrl;

    @Value("${upload.server.token}")
    String token;

    @Value("${upload.server.api.delete}")
    String deleteApiUrl;

}
