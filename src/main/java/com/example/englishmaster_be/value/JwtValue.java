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
public class JwtValue {


    @Value("${englishmaster.jwtSecret}")
    String jwtSecret;

    @Value("${englishmaster.salt.hash.jwtToken}")
    String saltHashToken;

    @Value("${englishmaster.jwtExpiration}")
    long jwtExpiration;

    @Value("${englishmaster.jwtRefreshExpirationMs}")
    long jwtRefreshExpirationMs;

}
