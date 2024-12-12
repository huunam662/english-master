package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.entity.InvalidTokenEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidTokenResponse {

    String token;

    LocalDateTime expireTime;

    public InvalidTokenResponse(InvalidTokenEntity invalidToken) {

        if(Objects.isNull(invalidToken)) return;

        this.token = invalidToken.getToken();
        this.expireTime = invalidToken.getExpireTime();
    }
}
