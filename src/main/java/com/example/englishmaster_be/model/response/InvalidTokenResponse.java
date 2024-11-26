package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.InvalidToken;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidTokenResponse {
    String token;
    LocalDateTime expireTime;

    public InvalidTokenResponse(InvalidToken invalidToken) {
        this.token = invalidToken.getToken();
        this.expireTime = invalidToken.getExpireTime();
    }
}
