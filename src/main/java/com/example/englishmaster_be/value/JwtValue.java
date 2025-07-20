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
public class JwtValue {


    @Value("${englishmaster.jwtSecret}")
    private String jwtSecret;

    @Value("${englishmaster.salt.hash.jwtToken}")
    private String saltHashToken;

    @Value("${englishmaster.jwtExpiration}")
    private long jwtExpiration;

    @Value("${englishmaster.jwtRefreshExpirationMs}")
    private long jwtRefreshExpirationMs;

}
